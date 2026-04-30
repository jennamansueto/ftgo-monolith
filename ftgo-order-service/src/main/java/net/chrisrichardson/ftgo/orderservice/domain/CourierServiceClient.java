package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.CourierActionDTO;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class CourierServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CourierServiceClient.class);
    private final RestTemplate restTemplate;
    private final String courierServiceUrl;

    public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
        this.restTemplate = restTemplate;
        this.courierServiceUrl = courierServiceUrl;
    }

    public long scheduleDelivery(long orderId, LocalDateTime readyBy) {
        try {
            ScheduleDeliveryResponse response = restTemplate.postForObject(
                courierServiceUrl + "/couriers/schedule-delivery",
                new ScheduleDeliveryRequest(orderId, readyBy),
                ScheduleDeliveryResponse.class);
            return response.getCourierId();
        } catch (Exception e) {
            logger.error("Error calling courier service", e);
            throw new RuntimeException("Courier service call failed", e);
        }
    }

    public List<CourierActionDTO> getCourierActionsForOrder(long courierId, long orderId) {
        try {
            ResponseEntity<List<CourierActionDTO>> response = restTemplate.exchange(
                courierServiceUrl + "/couriers/" + courierId + "/actions?orderId=" + orderId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CourierActionDTO>>() {});
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error fetching courier actions", e);
            return Collections.emptyList();
        }
    }
}
