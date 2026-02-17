package net.chrisrichardson.ftgo.orderservice.domain.client;

import net.chrisrichardson.ftgo.courierservice.api.CourierActionDto;
import net.chrisrichardson.ftgo.courierservice.api.GetCourierActionsForOrderResponse;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
      ScheduleDeliveryResponse response = restTemplate.postForObject(
              courierServiceUrl + "/couriers/schedule-delivery",
              request,
              ScheduleDeliveryResponse.class);
      return response.getCourierId();
    } catch (HttpClientErrorException e) {
      logger.error("Failed to schedule delivery for order {}: {}", orderId, e.getMessage());
      throw new RuntimeException("Failed to schedule delivery via courier service", e);
    }
  }

  public List<CourierActionDto> getCourierActionsForOrder(long courierId, long orderId) {
    try {
      GetCourierActionsForOrderResponse response = restTemplate.getForObject(
              courierServiceUrl + "/couriers/{courierId}/actions?orderId={orderId}",
              GetCourierActionsForOrderResponse.class,
              courierId, orderId);
      return response.getActions();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 404) {
        logger.warn("Courier {} not found when getting actions for order {}", courierId, orderId);
        return Collections.emptyList();
      }
      logger.error("Failed to get courier actions for courier {} order {}: {}", courierId, orderId, e.getMessage());
      throw new RuntimeException("Failed to get courier actions via courier service", e);
    }
  }
}
