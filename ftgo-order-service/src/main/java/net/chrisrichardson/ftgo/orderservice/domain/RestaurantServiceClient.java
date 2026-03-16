package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantWithMenuItemsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public Restaurant findRestaurant(long restaurantId) {
    String url = restaurantServiceUrl + "/restaurants/{restaurantId}/withMenuItems";
    try {
      GetRestaurantWithMenuItemsResponse body =
              restTemplate.getForObject(url, GetRestaurantWithMenuItemsResponse.class, restaurantId);

      if (body == null) {
        throw new RestaurantNotFoundException(restaurantId);
      }

      List<MenuItem> menuItems = body.getMenuItems().stream()
              .map(dto -> new MenuItem(dto.getId(), dto.getName(), dto.getPrice()))
              .collect(Collectors.toList());

      return new Restaurant(body.getId(), body.getName(), new RestaurantMenu(menuItems));

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("Error calling restaurant service for restaurantId={}", restaurantId, e);
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    } catch (RestClientException e) {
      logger.error("Error calling restaurant service for restaurantId={}", restaurantId, e);
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    }
  }
}
