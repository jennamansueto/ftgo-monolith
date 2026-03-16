package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Handles transactional order persistence. Extracted from OrderService so that
 * Spring AOP can intercept the @Transactional annotation — calling a
 * @Transactional method via self-invocation (this.method()) bypasses the proxy.
 */
public class OrderTransactionHelper {

  private OrderRepository orderRepository;
  private ConsumerService consumerService;
  private Optional<MeterRegistry> meterRegistry;

  public OrderTransactionHelper(OrderRepository orderRepository,
                                ConsumerService consumerService,
                                Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.consumerService = consumerService;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public Order createOrderInTransaction(long consumerId, Restaurant restaurant,
                                        List<MenuItemIdAndQuantity> lineItems) {
    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

    Order order = new Order(consumerId, restaurant, orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    // TODO - charge a credit card too

    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
    return lineItems.stream().map(li -> {
      MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }
}
