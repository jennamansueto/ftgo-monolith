package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class OrderTransactionService {

  private OrderRepository orderRepository;

  public OrderTransactionService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional
  public void acceptAndScheduleOrder(long orderId, LocalDateTime readyBy, long courierId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.acceptTicket(readyBy);
    order.schedule(courierId);
  }
}
