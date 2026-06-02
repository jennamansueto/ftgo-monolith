package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderLineItem;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Performs the transactional database work for order creation. Kept separate from
 * {@link OrderService} so that the remote (HTTP) restaurant lookup happens outside of any
 * transaction — see {@link OrderService#createOrder}.
 */
@Transactional
public class OrderPersistenceService {

  private final OrderRepository orderRepository;
  private final ConsumerService consumerService;
  private final Optional<MeterRegistry> meterRegistry;

  public OrderPersistenceService(OrderRepository orderRepository,
                                 ConsumerService consumerService,
                                 Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.consumerService = consumerService;
    this.meterRegistry = meterRegistry;
  }

  public Order saveOrder(long consumerId, long restaurantId, String restaurantName,
                         List<OrderLineItem> orderLineItems) {
    Order order = new Order(consumerId, restaurantId, restaurantName, orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    // TODO - charge a credit card too

    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }
}
