package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.AvailableCouriersResponse;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HTTP client that replaces the in-process call from OrderService into the (now extracted)
 * courier service. It mirrors the operations the monolith previously performed directly via
 * CourierRepository: finding available couriers and scheduling a delivery (selecting a courier
 * and assigning the pickup/dropoff actions). All HTTP plumbing lives here; the business logic
 * lives in the courier service.
 */
public class CourierServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public CourierServiceClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  /**
   * Asks the courier service to select an available courier and assign the delivery actions.
   *
   * @return the id of the courier the delivery was scheduled with
   */
  public long scheduleDelivery(long orderId, LocalDateTime dropoffTime) {
    String url = baseUrl + "/couriers/scheduleDelivery";
    try {
      ScheduleDeliveryResponse response =
              restTemplate.postForObject(url, new ScheduleDeliveryRequest(orderId, dropoffTime), ScheduleDeliveryResponse.class);
      return response.getCourierId();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NoAvailableCourierException();
      }
      throw new CourierServiceUnavailableException("Courier service returned an error: " + e.getStatusCode(), e);
    } catch (RestClientException e) {
      logger.error("Error calling courier service at {}", url, e);
      throw new CourierServiceUnavailableException("Unable to reach courier service at " + url, e);
    }
  }

  public List<Long> findAvailableCouriers() {
    String url = baseUrl + "/couriers/available";
    try {
      AvailableCouriersResponse response = restTemplate.getForObject(url, AvailableCouriersResponse.class);
      return response.getCourierIds();
    } catch (RestClientException e) {
      logger.error("Error calling courier service at {}", url, e);
      throw new CourierServiceUnavailableException("Unable to reach courier service at " + url, e);
    }
  }
}
