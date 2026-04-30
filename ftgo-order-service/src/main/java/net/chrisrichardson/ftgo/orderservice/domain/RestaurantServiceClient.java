package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantWithMenuResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
            ResponseEntity<GetRestaurantWithMenuResponse> response = restTemplate.getForEntity(
                    restaurantServiceUrl + "/restaurants/{restaurantId}/with-menu",
                    GetRestaurantWithMenuResponse.class,
                    restaurantId);
            GetRestaurantWithMenuResponse body = response.getBody();
            Restaurant restaurant = new Restaurant(body.getId(), body.getName(),
                    new RestaurantMenu(body.getMenu().getMenuItemDTOs().stream()
                            .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
                            .collect(Collectors.toList())));
            return Optional.of(restaurant);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            logger.error("Error calling restaurant service", e);
            throw new RuntimeException("Restaurant service call failed", e);
        } catch (Exception e) {
            logger.error("Error calling restaurant service", e);
            throw new RuntimeException("Restaurant service call failed", e);
        }
    }
}
