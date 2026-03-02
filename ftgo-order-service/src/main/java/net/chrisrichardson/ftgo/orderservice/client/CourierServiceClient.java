package net.chrisrichardson.ftgo.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class CourierServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String courierServiceUrl;

  public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
    this.restTemplate = restTemplate;
    this.courierServiceUrl = courierServiceUrl;
  }

  public List<AvailableCourierDTO> findAllAvailable() {
    String url = courierServiceUrl + "/couriers/available";
    logger.info("Calling courier service: GET {}", url);
    try {
      ResponseEntity<List<AvailableCourierDTO>> response = restTemplate.exchange(
              url,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<AvailableCourierDTO>>() {}
      );
      return response.getBody();
    } catch (Exception e) {
      logger.error("Error calling courier service findAllAvailable", e);
      throw new CourierServiceClientException("Failed to find available couriers", e);
    }
  }

  public void addAction(long courierId, String type, Long orderId, LocalDateTime time) {
    String url = courierServiceUrl + "/couriers/" + courierId + "/actions";
    logger.info("Calling courier service: POST {} type={} orderId={}", url, type, orderId);
    try {
      AddActionDTO request = new AddActionDTO(type, orderId, time);
      restTemplate.postForEntity(url, request, String.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("Courier not found: {}", courierId);
        throw new CourierServiceClientException("Courier not found with id " + courierId, e);
      }
      logger.error("Error calling courier service addAction for courier {}", courierId, e);
      throw new CourierServiceClientException("Failed to add action for courier " + courierId, e);
    } catch (Exception e) {
      logger.error("Error calling courier service addAction for courier {}", courierId, e);
      throw new CourierServiceClientException("Failed to add action for courier " + courierId, e);
    }
  }

}
