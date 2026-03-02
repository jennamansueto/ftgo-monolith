package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String consumerServiceUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);

    try {
      logger.info("Validating order for consumer {} with total {}", consumerId, orderTotal);
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      logger.info("Consumer {} validation succeeded", consumerId);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("Consumer {} not found", consumerId);
        throw new ConsumerNotFoundException(consumerId);
      }
      logger.error("Consumer validation failed for consumer {} with status {}", consumerId, e.getStatusCode(), e);
      throw new RuntimeException("Consumer validation failed: " + e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Error communicating with consumer service for consumer {}", consumerId, e);
      throw new RuntimeException("Error communicating with consumer service: " + e.getMessage(), e);
    }
  }
}
