package net.chrisrichardson.ftgo.orderservice.domain.client;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceClient.class);

  private final RestTemplate restTemplate;
  private final String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public Optional<Restaurant> findById(long restaurantId) {
    try {
      ResponseEntity<RestaurantDetailResponse> response = restTemplate.getForEntity(
              restaurantServiceUrl + "/restaurants/{id}/detail",
              RestaurantDetailResponse.class,
              restaurantId);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        RestaurantDetailResponse detail = response.getBody();
        List<MenuItem> menuItems = detail.getMenuItems().stream()
                .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
                .collect(Collectors.toList());
        Restaurant restaurant = new Restaurant(detail.getId(), detail.getName(), new RestaurantMenu(menuItems));
        return Optional.of(restaurant);
      }
      return Optional.empty();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.debug("Restaurant not found with id {}", restaurantId);
        return Optional.empty();
      }
      logger.error("HTTP error calling restaurant service for id {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to call restaurant service", e);
    } catch (RestClientException e) {
      logger.error("Error calling restaurant service for id {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to call restaurant service", e);
    }
  }
}
