package net.chrisrichardson.ftgo.consumerservice.microservice;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class ConsumerMicroservice {

  private ConsumerEntityRepository consumerRepository;

  public ConsumerMicroservice(ConsumerEntityRepository consumerRepository) {
    this.consumerRepository = consumerRepository;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    Optional<ConsumerEntity> consumer = consumerRepository.findById(consumerId);
    consumer.orElseThrow(ConsumerNotFoundException::new).validateOrderByConsumer(orderTotal);
  }

  public ConsumerEntity create(PersonName name) {
    ConsumerEntity consumer = consumerRepository.save(new ConsumerEntity(name));
    return consumer;
  }

  public Optional<ConsumerEntity> findById(long consumerId) {
    return consumerRepository.findById(consumerId);
  }
}
