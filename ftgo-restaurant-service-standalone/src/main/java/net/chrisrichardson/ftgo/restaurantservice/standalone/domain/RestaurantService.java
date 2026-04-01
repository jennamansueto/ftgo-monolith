package net.chrisrichardson.ftgo.restaurantservice.standalone.domain;

import net.chrisrichardson.ftgo.restaurantservice.standalone.Money;
import net.chrisrichardson.ftgo.restaurantservice.standalone.Address;
import net.chrisrichardson.ftgo.restaurantservice.standalone.web.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.standalone.web.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.standalone.web.RestaurantMenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class RestaurantService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  public Restaurant create(CreateRestaurantRequest request) {
    Restaurant restaurant = new Restaurant(request.getName(), request.getAddress(), makeRestaurantMenu(request.getMenu()));
    restaurantRepository.save(restaurant);
    return restaurant;
  }

  private RestaurantMenu makeRestaurantMenu(RestaurantMenuDTO menu) {
    return new RestaurantMenu(menu.getMenuItemDTOs().stream().map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice())).collect(Collectors.toList()));
  }

  public Optional<Restaurant> findById(long restaurantId) {
    return restaurantRepository.findById(restaurantId);
  }
}
