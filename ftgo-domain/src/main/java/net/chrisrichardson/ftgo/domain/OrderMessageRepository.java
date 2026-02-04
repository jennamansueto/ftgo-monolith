package net.chrisrichardson.ftgo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMessageRepository extends JpaRepository<OrderMessage, Long> {

  List<OrderMessage> findByOrderId(Long orderId);

  List<OrderMessage> findByConsumerId(Long consumerId);

  List<OrderMessage> findByRestaurantId(Long restaurantId);
}
