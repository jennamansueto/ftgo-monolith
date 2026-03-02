package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Handles transactional persistence of orders.
 * Extracted into a separate bean so that Spring's proxy-based @Transactional
 * works correctly when called from OrderService (avoids self-invocation issue).
 */
@Transactional
public class OrderPersistenceService {

  private OrderRepository orderRepository;
  private Optional<MeterRegistry> meterRegistry;

  public OrderPersistenceService(OrderRepository orderRepository, Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public Order persistOrder(Order order) {
    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());
    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }
}
