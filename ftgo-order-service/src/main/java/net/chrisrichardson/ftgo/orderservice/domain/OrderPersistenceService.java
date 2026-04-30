package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OrderPersistenceService {

  private final OrderRepository orderRepository;
  private final Optional<MeterRegistry> meterRegistry;

  public OrderPersistenceService(OrderRepository orderRepository, Optional<MeterRegistry> meterRegistry) {
    this.orderRepository = orderRepository;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public Order saveOrder(Order order) {
    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }
}
