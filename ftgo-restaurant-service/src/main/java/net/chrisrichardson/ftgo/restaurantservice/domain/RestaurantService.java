package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.persistence.MenuItemEntity;
import net.chrisrichardson.ftgo.restaurantservice.persistence.RestaurantEntity;
import net.chrisrichardson.ftgo.restaurantservice.persistence.RestaurantEntityRepository;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class RestaurantService {

  @Autowired
  private RestaurantEntityRepository restaurantEntityRepository;

  public RestaurantEntity create(CreateRestaurantRequest request) {
    RestaurantEntity restaurant = new RestaurantEntity(request.getName(), request.getAddress(), makeMenuItems(request.getMenu()));
    restaurantEntityRepository.save(restaurant);
    return restaurant;
  }

  private List<MenuItemEntity> makeMenuItems(RestaurantMenuDTO menu) {
    return menu.getMenuItemDTOs().stream().map(mi -> new MenuItemEntity(mi.getId(), mi.getName(), mi.getPrice())).collect(Collectors.toList());
  }

  public Optional<RestaurantEntity> findById(long restaurantId) {
    return restaurantEntityRepository.findById(restaurantId);
  }
}
