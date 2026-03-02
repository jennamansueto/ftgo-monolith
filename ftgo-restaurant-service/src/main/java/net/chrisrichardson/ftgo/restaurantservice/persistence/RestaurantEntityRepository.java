package net.chrisrichardson.ftgo.restaurantservice.persistence;

import org.springframework.data.repository.CrudRepository;

public interface RestaurantEntityRepository extends CrudRepository<RestaurantEntity, Long> {
}
