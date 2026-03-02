package net.chrisrichardson.ftgo.courierservice.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourierEntityRepository extends CrudRepository<CourierEntity, Long> {

  @Query("SELECT c FROM CourierEntity c WHERE c.available = true")
  List<CourierEntity> findAllAvailable();

}
