package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantWithMenuResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RestaurantServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceClient.class);

    private final RestTemplate restTemplate;
    private final String restaurantServiceUrl;

    public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
        this.restTemplate = restTemplate;
        this.restaurantServiceUrl = restaurantServiceUrl;
    }

    public GetRestaurantWithMenuResponse findRestaurantWithMenu(long restaurantId) {
        String url = restaurantServiceUrl + "/restaurants/" + restaurantId + "/with-menu";
        try {
            return restTemplate.getForObject(url, GetRestaurantWithMenuResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("Restaurant not found: {}", restaurantId);
                throw new RestaurantNotFoundException(restaurantId);
            }
            logger.error("Error calling restaurant service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch restaurant " + restaurantId, e);
        } catch (Exception e) {
            logger.error("Error calling restaurant service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch restaurant " + restaurantId, e);
        }
    }
}
