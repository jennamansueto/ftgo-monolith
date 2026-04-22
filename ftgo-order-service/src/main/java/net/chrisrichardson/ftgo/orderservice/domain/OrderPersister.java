package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderLineItem;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class OrderPersister {

  private final OrderRepository orderRepository;
  private final ConsumerService consumerService;
  private final Optional<MeterRegistry> meterRegistry;

  public OrderPersister(OrderRepository orderRepository,
                        ConsumerService consumerService,
                        Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.consumerService = consumerService;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public Order persistOrder(long consumerId,
                            GetRestaurantResponse restaurant,
                            List<OrderLineItem> orderLineItems) {
    Order order = new Order(consumerId, restaurant.getId(), restaurant.getName(), orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    orderRepository.save(order);

    meterRegistry.ifPresent(mr -> mr.counter("approved_orders").increment());
    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }
}
