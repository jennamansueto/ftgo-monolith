package net.chrisrichardson.ftgo.restaurantservice.standalone.domain;

import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
}
