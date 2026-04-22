package net.chrisrichardson.ftgo.orderservice.courier;

import net.chrisrichardson.ftgo.courierservice.api.AssignCourierActionsRequest;
import net.chrisrichardson.ftgo.courierservice.api.AvailableCourierDTO;
import net.chrisrichardson.ftgo.courierservice.api.CourierActionDTO;
import net.chrisrichardson.ftgo.courierservice.api.CourierNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class CourierServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(CourierServiceClient.class);

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public CourierServiceClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = stripTrailingSlash(baseUrl);
  }

  public List<AvailableCourierDTO> findAvailableCouriers() {
    String url = baseUrl + "/couriers/available";
    try {
      ResponseEntity<List<AvailableCourierDTO>> response = restTemplate.exchange(
              url,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              new ParameterizedTypeReference<List<AvailableCourierDTO>>() {});
      List<AvailableCourierDTO> body = response.getBody();
      return body == null ? Collections.emptyList() : body;
    } catch (RestClientException e) {
      throw wrap("findAvailableCouriers", url, e);
    }
  }

  public List<CourierActionDTO> assignOrderToCourier(long courierId, long orderId, LocalDateTime readyBy) {
    String url = baseUrl + "/couriers/" + courierId + "/actions";
    try {
      ResponseEntity<List<CourierActionDTO>> response = restTemplate.exchange(
              url,
              HttpMethod.POST,
              new HttpEntity<>(new AssignCourierActionsRequest(orderId, readyBy)),
              new ParameterizedTypeReference<List<CourierActionDTO>>() {});
      List<CourierActionDTO> body = response.getBody();
      return body == null ? Collections.emptyList() : body;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new CourierNotFoundException(courierId);
      }
      throw wrap("assignOrderToCourier", url, e);
    } catch (RestClientException e) {
      throw wrap("assignOrderToCourier", url, e);
    }
  }

  public List<CourierActionDTO> getActionsForOrder(long courierId, long orderId) {
    String url = baseUrl + "/couriers/" + courierId + "/orders/" + orderId + "/actions";
    try {
      ResponseEntity<List<CourierActionDTO>> response = restTemplate.exchange(
              url,
              HttpMethod.GET,
              HttpEntity.EMPTY,
              new ParameterizedTypeReference<List<CourierActionDTO>>() {});
      List<CourierActionDTO> body = response.getBody();
      return body == null ? Collections.emptyList() : body;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new CourierNotFoundException(courierId);
      }
      throw wrap("getActionsForOrder", url, e);
    } catch (RestClientException e) {
      throw wrap("getActionsForOrder", url, e);
    }
  }

  private CourierServiceUnavailableException wrap(String operation, String url, RestClientException e) {
    if (e instanceof ResourceAccessException) {
      logger.warn("Courier service network error during {} ({}): {}", operation, url, e.getMessage());
    } else if (e instanceof HttpStatusCodeException) {
      logger.warn("Courier service returned error during {} ({}): {} {}",
              operation, url, ((HttpStatusCodeException) e).getStatusCode(), ((HttpStatusCodeException) e).getResponseBodyAsString());
    } else {
      logger.warn("Courier service error during {} ({}): {}", operation, url, e.getMessage());
    }
    return new CourierServiceUnavailableException(
            "Courier service call failed: " + operation + " (" + url + ")", e);
  }

  private static String stripTrailingSlash(String s) {
    return s != null && s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
  }

}
