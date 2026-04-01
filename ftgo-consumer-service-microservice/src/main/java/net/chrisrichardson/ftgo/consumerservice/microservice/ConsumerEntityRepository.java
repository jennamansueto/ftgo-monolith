package net.chrisrichardson.ftgo.consumerservice.microservice;

import org.springframework.data.repository.CrudRepository;

public interface ConsumerEntityRepository extends CrudRepository<ConsumerEntity, Long> {
}
