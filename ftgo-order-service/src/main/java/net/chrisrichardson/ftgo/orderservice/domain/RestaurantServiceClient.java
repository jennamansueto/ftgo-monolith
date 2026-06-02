package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client that replaces the in-process call to the restaurant service's repository.
 *
 * <p>Contains HTTP plumbing only — it delegates to the restaurant service's REST API and
 * translates transport-level outcomes into the order service's domain exceptions:
 * a 404 becomes {@link RestaurantNotFoundException}; any other transport failure becomes
 * {@link RestaurantServiceUnavailableException}.
 */
public class RestaurantServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String restaurantServiceUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String restaurantServiceUrl) {
    this.restTemplate = restTemplate;
    this.restaurantServiceUrl = restaurantServiceUrl;
  }

  public RestaurantDTO findRestaurant(long restaurantId) {
    String url = restaurantServiceUrl + "/restaurants/" + restaurantId;
    try {
      return restTemplate.getForObject(url, RestaurantDTO.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("Error fetching restaurant {} from {}: {}", restaurantId, url, e.getStatusCode(), e);
      throw new RestaurantServiceUnavailableException(restaurantId, e);
    } catch (RestClientException e) {
      logger.error("Failed to reach restaurant service at {} for restaurant {}", url, restaurantId, e);
      throw new RestaurantServiceUnavailableException(restaurantId, e);
    }
  }
}
