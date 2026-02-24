package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.orderservice.client.RestaurantServiceClient;
import net.chrisrichardson.ftgo.orderservice.client.RestaurantValidationResult;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;

import java.util.List;

/**
 * Facade that orchestrates order creation by performing the HTTP call to the
 * Restaurant Service BEFORE delegating to the transactional OrderService.
 *
 * This class is intentionally NOT @Transactional so the remote HTTP call
 * does not hold a database connection idle during network I/O.
 *
 * Note: if the DB write fails after the successful remote validation call,
 * there is no automatic rollback of the remote call. This is an accepted
 * trade-off of the microservice extraction.
 */
public class OrderServiceFacade {

  private final OrderService orderService;
  private final RestaurantServiceClient restaurantServiceClient;

  public OrderServiceFacade(OrderService orderService, RestaurantServiceClient restaurantServiceClient) {
    this.orderService = orderService;
    this.restaurantServiceClient = restaurantServiceClient;
  }

  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    // Step 1: HTTP call to Restaurant Service - NOT inside a transaction
    RestaurantValidationResult validationResult =
            restaurantServiceClient.validateMenuItems(restaurantId, lineItems);

    // Step 2: Transactional database work - calls through Spring proxy
    return orderService.createOrderTransactional(consumerId, restaurantId, lineItems, validationResult);
  }
}
