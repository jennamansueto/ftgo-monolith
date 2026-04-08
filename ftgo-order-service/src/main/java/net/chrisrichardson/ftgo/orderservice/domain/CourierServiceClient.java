package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.AssignDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.AssignDeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

public class CourierServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CourierServiceClient.class);

    private final RestTemplate restTemplate;
    private final String courierServiceUrl;

    public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
        this.restTemplate = restTemplate;
        this.courierServiceUrl = courierServiceUrl;
    }

    public long assignDelivery(long orderId, LocalDateTime pickupTime, LocalDateTime dropoffTime) {
        String url = courierServiceUrl + "/couriers/assign-delivery";
        AssignDeliveryRequest request = new AssignDeliveryRequest(orderId, pickupTime, dropoffTime);
        try {
            AssignDeliveryResponse response = restTemplate.postForObject(url, request, AssignDeliveryResponse.class);
            if (response == null) {
                throw new RuntimeException("Received null response from courier service for order " + orderId);
            }
            return response.getCourierId();
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                logger.error("No courier available for order: {}", orderId);
                throw new RuntimeException("No courier available for delivery of order " + orderId, e);
            }
            logger.error("Error calling courier service: {}", e.getMessage());
            throw new RuntimeException("Failed to assign delivery for order " + orderId, e);
        } catch (Exception e) {
            logger.error("Error calling courier service: {}", e.getMessage());
            throw new RuntimeException("Failed to assign delivery for order " + orderId, e);
        }
    }
}
