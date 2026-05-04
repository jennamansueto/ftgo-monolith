package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.domain.RestaurantMenu;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantServiceClient {

    private final RestTemplate restTemplate;
    private final String restaurantServiceUrl;

    public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
        this.restTemplate = restTemplate;
        this.restaurantServiceUrl = restaurantServiceUrl;
    }

    public Restaurant findById(long restaurantId) {
        String url = restaurantServiceUrl + "/restaurants/" + restaurantId;
        try {
            ResponseEntity<RestaurantResponse> response =
                    restTemplate.getForEntity(url, RestaurantResponse.class);
            return mapToRestaurant(response.getBody());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RestaurantNotFoundException(restaurantId);
            }
            throw new RuntimeException("Failed to fetch restaurant: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch restaurant: " + e.getMessage(), e);
        }
    }

    private Restaurant mapToRestaurant(RestaurantResponse response) {
        List<MenuItem> menuItems = response.getMenuItems().stream()
                .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
                .collect(Collectors.toList());
        return new Restaurant(response.getId(), response.getName(), new RestaurantMenu(menuItems));
    }

    public static class RestaurantResponse {
        private Long id;
        private String name;
        private List<MenuItemResponse> menuItems;

        public RestaurantResponse() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MenuItemResponse> getMenuItems() {
            return menuItems;
        }

        public void setMenuItems(List<MenuItemResponse> menuItems) {
            this.menuItems = menuItems;
        }
    }

    public static class MenuItemResponse {
        private String id;
        private String name;
        private Money price;

        public MenuItemResponse() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Money getPrice() {
            return price;
        }

        public void setPrice(Money price) {
            this.price = price;
        }
    }
}
