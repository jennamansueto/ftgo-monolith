package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.domain.InvalidMenuItemIdException;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantNotFoundException;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemValidationResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.ValidateMenuItemsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public RestaurantValidationResult validateMenuItems(long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
    List<String> menuItemIds = lineItems.stream()
            .map(MenuItemIdAndQuantity::getMenuItemId)
            .collect(Collectors.toList());

    ValidateMenuItemsRequest request = new ValidateMenuItemsRequest(menuItemIds);

    String url = restaurantServiceUrl + "/restaurants/" + restaurantId + "/validateMenuItems";

    try {
      ResponseEntity<MenuItemValidationResponse> response = restTemplate.postForEntity(
              url, request, MenuItemValidationResponse.class);

      MenuItemValidationResponse body = response.getBody();

      Map<String, MenuItemDTO> menuItemMap = body.getMenuItems().stream()
              .collect(Collectors.toMap(MenuItemDTO::getId, mi -> mi));

      return new RestaurantValidationResult(body.getRestaurantId(), body.getRestaurantName(), menuItemMap);

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("Restaurant {} not found or menu item validation failed", restaurantId);
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("HTTP error communicating with Restaurant Service for restaurant {}: {}", restaurantId, e.getStatusCode(), e);
      throw new RuntimeException("Failed to communicate with Restaurant Service", e);
    } catch (RestClientException e) {
      logger.error("Error communicating with Restaurant Service for restaurant {}", restaurantId, e);
      throw new RuntimeException("Failed to communicate with Restaurant Service", e);
    }
  }
}
