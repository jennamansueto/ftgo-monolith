package net.chrisrichardson.ftgo.restaurantservice.domain;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderMessage;
import net.chrisrichardson.ftgo.domain.OrderMessageRepository;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.domain.RestaurantRepository;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class RestaurantService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderMessageRepository orderMessageRepository;

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

  public OrderMessage sendMessage(long restaurantId, long orderId, String message) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (!order.getRestaurant().getId().equals(restaurant.getId())) {
      throw new OrderNotBelongToRestaurantException(orderId, restaurantId);
    }

    OrderMessage orderMessage = new OrderMessage(order, message);
    orderMessageRepository.save(orderMessage);
    return orderMessage;
  }
}
