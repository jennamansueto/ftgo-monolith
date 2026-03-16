package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

public class CourierServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String courierServiceUrl;

  public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
    this.restTemplate = restTemplate;
    this.courierServiceUrl = courierServiceUrl;
  }

  public long scheduleDelivery(long orderId, LocalDateTime readyBy) {
    ScheduleDeliveryRequest request = new ScheduleDeliveryRequest(orderId, readyBy);
    try {
      ResponseEntity<ScheduleDeliveryResponse> response = restTemplate.postForEntity(
              courierServiceUrl + "/couriers/scheduleDelivery",
              request,
              ScheduleDeliveryResponse.class);
      return response.getBody().getCourierId();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("No courier available for order {}", orderId);
        throw new NoCourierAvailableException(orderId);
      }
      logger.error("Error calling courier service for order {}: {}", orderId, e.getMessage());
      throw new CourierServiceException("Failed to schedule delivery for order " + orderId, e);
    } catch (RestClientException e) {
      logger.error("Error calling courier service for order {}: {}", orderId, e.getMessage());
      throw new CourierServiceException("Failed to schedule delivery for order " + orderId, e);
    }
  }
}
