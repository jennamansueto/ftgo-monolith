package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.ActionRequest;
import net.chrisrichardson.ftgo.courierservice.api.GetCourierResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class CourierServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(CourierServiceClient.class);

  private final RestTemplate restTemplate;
  private final String courierServiceUrl;

  public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
    this.restTemplate = restTemplate;
    this.courierServiceUrl = courierServiceUrl;
  }

  public List<GetCourierResponse> findAllAvailable() {
    try {
      ResponseEntity<List<GetCourierResponse>> response = restTemplate.exchange(
              courierServiceUrl + "/couriers/available",
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<GetCourierResponse>>() {});
      return response.getBody();
    } catch (HttpClientErrorException e) {
      logger.error("Error fetching available couriers: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw new RuntimeException("Failed to fetch available couriers from Courier Service", e);
    } catch (Exception e) {
      logger.error("Network error communicating with Courier Service", e);
      throw new RuntimeException("Failed to communicate with Courier Service", e);
    }
  }

  public void addActions(long courierId, List<ActionRequest> actions) {
    try {
      restTemplate.postForEntity(
              courierServiceUrl + "/couriers/{courierId}/actions",
              actions,
              String.class,
              courierId);
    } catch (HttpClientErrorException e) {
      logger.error("Error adding actions to courier {}: {} {}", courierId, e.getStatusCode(), e.getResponseBodyAsString());
      throw new RuntimeException("Failed to add actions to courier " + courierId, e);
    } catch (Exception e) {
      logger.error("Network error communicating with Courier Service", e);
      throw new RuntimeException("Failed to communicate with Courier Service", e);
    }
  }

}
