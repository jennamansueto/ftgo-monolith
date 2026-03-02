package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
// RestaurantNotFoundException is in this package (orderservice.domain)
import net.chrisrichardson.ftgo.domain.OrderLineItem;
import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Handles transactional persistence of orders.
 * Extracted into a separate bean so that Spring's proxy-based @Transactional
 * works correctly when called from OrderService (avoids self-invocation issue).
 */
@Transactional
public class OrderPersistenceService {

  private OrderRepository orderRepository;
  private RestaurantRepository restaurantRepository;
  private Optional<MeterRegistry> meterRegistry;

  public OrderPersistenceService(OrderRepository orderRepository,
                                  RestaurantRepository restaurantRepository,
                                  Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Loads the restaurant and builds the Order within a read-only transaction,
   * ensuring lazy-loaded collections (e.g. menuItems) are initialized before
   * the transaction ends.
   */
  @Transactional(readOnly = true)
  public Order prepareOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    List<OrderLineItem> orderLineItems = lineItems.stream().map(li -> {
      MenuItem om = restaurant.findMenuItem(li.getMenuItemId())
              .orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());

    return new Order(consumerId, restaurant, orderLineItems);
  }

  @Transactional
  public Order persistOrder(Order order) {
    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());
    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }
}
