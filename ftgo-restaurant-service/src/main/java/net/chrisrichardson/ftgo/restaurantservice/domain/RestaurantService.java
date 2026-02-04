package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.restaurantservice.events.AddMenuItemRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.UpdateMenuItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

  public List<MenuItem> getMenu(long restaurantId) {
    return findById(restaurantId)
            .map(Restaurant::getMenuItems)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
  }

  public MenuItem addMenuItem(long restaurantId, AddMenuItemRequest request) {
    Restaurant restaurant = findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    if (restaurant.hasMenuItem(request.getId())) {
      throw new DuplicateMenuItemException(request.getId());
    }

    MenuItem newItem = new MenuItem(request.getId(), request.getName(), request.getPrice());
    restaurant.addMenuItem(newItem);
    restaurantRepository.save(restaurant);
    return newItem;
  }

  public MenuItem updateMenuItem(long restaurantId, String itemId, UpdateMenuItemRequest request) {
    Restaurant restaurant = findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    if (!restaurant.updateMenuItem(itemId, request.getName(), request.getPrice())) {
      throw new MenuItemNotFoundException(itemId);
    }

    restaurantRepository.save(restaurant);
    return restaurant.findMenuItem(itemId).orElseThrow(() -> new MenuItemNotFoundException(itemId));
  }

  public void removeMenuItem(long restaurantId, String itemId) {
    Restaurant restaurant = findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    if (!restaurant.removeMenuItem(itemId)) {
      throw new MenuItemNotFoundException(itemId);
    }

    restaurantRepository.save(restaurant);
  }
}
