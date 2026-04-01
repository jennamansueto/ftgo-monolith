package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantServiceClient {

  private final RestTemplate restTemplate;
  private final String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public Optional<RestaurantDTO> findById(long restaurantId) {
    try {
      ResponseEntity<Map> response = restTemplate.getForEntity(
              restaurantServiceUrl + "/restaurants/{id}",
              Map.class,
              restaurantId);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return Optional.of(toRestaurantDTO(response.getBody()));
      }
      return Optional.empty();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return Optional.empty();
      }
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private RestaurantDTO toRestaurantDTO(Map<String, Object> body) {
    Long id = body.get("id") instanceof Integer ? ((Integer) body.get("id")).longValue() : (Long) body.get("id");
    String name = (String) body.get("name");

    List<Map<String, Object>> menuItemMaps = (List<Map<String, Object>>) body.get("menuItems");
    List<RestaurantDTO.MenuItemInfo> menuItems = menuItemMaps.stream()
            .map(mi -> new RestaurantDTO.MenuItemInfo(
                    (String) mi.get("id"),
                    (String) mi.get("name"),
                    new Money((String) mi.get("price"))
            ))
            .collect(Collectors.toList());

    return new RestaurantDTO(id, name, menuItems);
  }
}
