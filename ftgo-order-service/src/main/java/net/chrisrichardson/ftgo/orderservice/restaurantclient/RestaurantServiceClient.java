package net.chrisrichardson.ftgo.orderservice.restaurantclient;

import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestaurantServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public RestaurantServiceClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  public GetRestaurantResponse findRestaurant(long restaurantId) {
    String url = baseUrl + "/restaurants/" + restaurantId;
    try {
      ResponseEntity<GetRestaurantResponse> response =
              restTemplate.getForEntity(url, GetRestaurantResponse.class);
      if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
        throw new RestaurantServiceUnavailableException("Unexpected status " + response.getStatusCode() + " from " + url);
      }
      return response.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RestaurantNotFoundException(restaurantId);
      }
      logger.error("HTTP error calling restaurant service at {}: {}", url, e.getStatusCode());
      throw new RestaurantServiceUnavailableException("Restaurant service returned " + e.getStatusCode(), e);
    } catch (RestClientException e) {
      logger.error("Network error calling restaurant service at {}", url, e);
      throw new RestaurantServiceUnavailableException("Restaurant service unreachable at " + baseUrl, e);
    }
  }
}
