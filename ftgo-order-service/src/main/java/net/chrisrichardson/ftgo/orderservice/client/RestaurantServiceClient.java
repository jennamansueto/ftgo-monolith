package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.orderservice.domain.RestaurantNotFoundException;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestaurantServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public RestaurantMenuDTO findRestaurantMenu(long restaurantId) {
    try {
      ResponseEntity<RestaurantMenuDTO> response = restTemplate.getForEntity(
              restaurantServiceUrl + "/restaurants/{restaurantId}/menu",
              RestaurantMenuDTO.class,
              restaurantId);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    } catch (RestClientException e) {
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    }
  }

  public boolean restaurantExists(long restaurantId) {
    try {
      restTemplate.getForEntity(
              restaurantServiceUrl + "/restaurants/{restaurantId}",
              Void.class,
              restaurantId);
      return true;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return false;
      }
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    } catch (RestClientException e) {
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    }
  }

  @SuppressWarnings("unchecked")
  public String getRestaurantName(long restaurantId) {
    try {
      ResponseEntity<Map> response = restTemplate.getForEntity(
              restaurantServiceUrl + "/restaurants/{restaurantId}",
              Map.class,
              restaurantId);
      Map body = response.getBody();
      if (body != null && body.containsKey("name")) {
        return (String) body.get("name");
      }
      return null;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    } catch (RestClientException e) {
      logger.error("Error calling restaurant service for restaurant {}: {}", restaurantId, e.getMessage());
      throw new RuntimeException("Failed to communicate with restaurant service", e);
    }
  }
}
