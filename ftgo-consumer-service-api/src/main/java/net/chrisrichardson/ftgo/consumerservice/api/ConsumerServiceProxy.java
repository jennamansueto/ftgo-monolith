package net.chrisrichardson.ftgo.consumerservice.api;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = String.format("%s/consumers/%d/validate", consumerServiceUrl, consumerId);
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ConsumerVerificationException(
                String.format("Consumer validation failed for consumer %d with status %s",
                        consumerId, response.getStatusCode()));
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerVerificationException(
                String.format("Consumer %d not found", consumerId), e);
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerVerificationException(
                String.format("Consumer %d failed order validation", consumerId), e);
      }
      throw new ConsumerVerificationException(
              String.format("Consumer validation failed for consumer %d: %s",
                      consumerId, e.getMessage()), e);
    } catch (HttpServerErrorException e) {
      logger.error("Consumer service error at {}: {}", consumerServiceUrl, e.getStatusCode(), e);
      throw new ConsumerServiceUnavailableException(
              "Consumer service returned error: " + e.getMessage(), e);
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable at {}", consumerServiceUrl, e);
      throw new ConsumerServiceUnavailableException(
              "Consumer service is unavailable: " + e.getMessage(), e);
    }
  }
}
