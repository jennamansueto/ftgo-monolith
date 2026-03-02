package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderLineItem;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Handles the transactional portion of order creation. Extracted into a separate
 * Spring bean so that @Transactional is honored (avoids Spring's self-invocation
 * proxy bypass when called from OrderService).
 */
@Transactional
public class OrderCreationService {

  private final OrderRepository orderRepository;
  private final Optional<MeterRegistry> meterRegistry;
  private final ConsumerService consumerService;

  public OrderCreationService(OrderRepository orderRepository,
                               Optional<MeterRegistry> meterRegistry,
                               ConsumerService consumerService) {
    this.orderRepository = orderRepository;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
  }

  @Transactional
  public Order createOrderTransactional(long consumerId, long restaurantId,
                                         List<MenuItemIdAndQuantity> lineItems,
                                         RestaurantMenuDTO restaurantMenu) {
    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurantMenu);

    Order order = new Order(consumerId, restaurantId, orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    // TODO - charge a credit card too

    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, RestaurantMenuDTO restaurantMenu) {
    return lineItems.stream().map(li -> {
      MenuItemDTO om = restaurantMenu.getMenuItemDTOs().stream()
              .filter(mi -> mi.getId().equals(li.getMenuItemId()))
              .findFirst()
              .orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }
}
