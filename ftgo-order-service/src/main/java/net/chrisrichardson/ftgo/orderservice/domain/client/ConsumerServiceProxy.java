package net.chrisrichardson.ftgo.orderservice.domain.client;

import net.chrisrichardson.ftgo.common.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

public class ConsumerServiceProxy implements ConsumerServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    String requestBody = "{\"orderTotal\":\"" + orderTotal.asString() + "\"}";
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        logger.debug("Consumer {} validated successfully for order total {}", consumerId, orderTotal);
        return;
      }

      logger.error("Consumer validation failed with status: {}", response.getStatusCode());
      throw new ConsumerValidationException("Consumer validation failed with status: " + response.getStatusCode());

    } catch (HttpClientErrorException e) {
      HttpStatus status = e.getStatusCode();
      if (Objects.equals(status, HttpStatus.NOT_FOUND)) {
        logger.error("Consumer {} not found", consumerId);
        throw new ConsumerValidationException("Consumer not found: " + consumerId);
      } else if (Objects.equals(status, HttpStatus.UNPROCESSABLE_ENTITY)) {
        logger.error("Consumer {} validation failed for order total {}", consumerId, orderTotal);
        throw new ConsumerValidationException("Consumer validation failed for consumer: " + consumerId);
      }
      logger.error("HTTP error calling consumer service: {} {}", e.getStatusCode(), e.getMessage());
      throw new ConsumerValidationException("Consumer service returned error: " + e.getStatusCode());

    } catch (ResourceAccessException e) {
      logger.error("Cannot connect to consumer service at {}: {}", consumerServiceUrl, e.getMessage());
      throw new ConsumerServiceUnavailableException("Consumer service unavailable at: " + consumerServiceUrl, e);
    }
  }
}
