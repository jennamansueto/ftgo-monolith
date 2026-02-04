package net.chrisrichardson.ftgo.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderMessageRepository extends CrudRepository<OrderMessage, Long> {
  List<OrderMessage> findByOrderId(Long orderId);
}
