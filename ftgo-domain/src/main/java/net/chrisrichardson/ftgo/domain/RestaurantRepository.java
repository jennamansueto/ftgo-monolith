package net.chrisrichardson.ftgo.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

  @EntityGraph(attributePaths = "menuItems")
  Optional<Restaurant> findWithMenuItemsById(Long id);
}
