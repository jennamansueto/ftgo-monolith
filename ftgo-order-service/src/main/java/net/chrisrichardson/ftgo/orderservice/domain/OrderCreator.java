package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;

import java.util.List;

/**
 * Orchestrates order creation across the service boundary. This class is intentionally NOT
 * {@code @Transactional}: it performs the remote HTTP consumer-validation call OUTSIDE of any
 * database transaction so that a remote call never holds a JDBC connection open while waiting on
 * network I/O (which would exhaust the connection pool under load).
 *
 * Flow:
 *   1. {@link OrderService#buildOrder} (read-only tx) loads the restaurant and builds the order.
 *   2. {@link ConsumerServiceClient#validateOrderForConsumer} (no tx) validates over HTTP.
 *   3. {@link OrderService#saveOrder} (write tx) persists the order.
 *
 * Trade-off: because validation and persistence are now in separate transactions, the operation is
 * no longer atomic. If the HTTP validation succeeds but the subsequent DB save fails, the remote
 * call cannot be rolled back. Validation has no side effects today, so this is safe; see the PR
 * description for details.
 */
public class OrderCreator {

  private final OrderService orderService;
  private final ConsumerServiceClient consumerServiceClient;

  public OrderCreator(OrderService orderService, ConsumerServiceClient consumerServiceClient) {
    this.orderService = orderService;
    this.consumerServiceClient = consumerServiceClient;
  }

  public Order createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
    Order order = orderService.buildOrder(consumerId, restaurantId, lineItems);

    consumerServiceClient.validateOrderForConsumer(consumerId, order.getOrderTotal());

    return orderService.saveOrder(order);
  }
}
