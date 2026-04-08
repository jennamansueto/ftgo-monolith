package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class OrderTransactionHelper {

  private final OrderRepository orderRepository;

  public OrderTransactionHelper(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Order acceptTicket(long orderId, LocalDateTime readyBy) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.acceptTicket(readyBy);
    return order;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void assignCourierToOrder(long orderId, long courierId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.scheduleWithCourierId(courierId);
  }
}
