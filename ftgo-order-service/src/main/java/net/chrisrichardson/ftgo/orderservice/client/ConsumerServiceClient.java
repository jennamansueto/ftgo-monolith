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

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal.asString() != null ? new java.math.BigDecimal(orderTotal.asString()) : java.math.BigDecimal.ZERO);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ConsumerValidationFailedException("Consumer validation failed for consumerId: " + consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("Consumer not found: consumerId={}", consumerId);
        throw new ConsumerValidationFailedException("Consumer not found: " + consumerId);
      } else if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        logger.error("Consumer verification failed: consumerId={}", consumerId);
        throw new ConsumerValidationFailedException("Consumer verification failed for consumerId: " + consumerId);
      } else {
        logger.error("Consumer service returned error: status={}, consumerId={}", e.getStatusCode(), consumerId);
        throw new ConsumerValidationFailedException("Consumer validation failed for consumerId: " + consumerId);
      }
    }
  }
}
