package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    logger.info("Validating order for consumer {} via {}", consumerId, url);

    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal.asString() != null ? new java.math.BigDecimal(orderTotal.asString()) : java.math.BigDecimal.ZERO);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<ValidateOrderForConsumerResponse> response =
              restTemplate.postForEntity(url, entity, ValidateOrderForConsumerResponse.class);

      ValidateOrderForConsumerResponse body = response.getBody();
      if (body == null || !body.isValid()) {
        String message = body != null ? body.getMessage() : "Consumer validation failed";
        throw new ConsumerValidationFailedException(message);
      }
    } catch (HttpClientErrorException e) {
      logger.error("Consumer validation failed for consumer {}: {}", consumerId, e.getMessage());
      throw new ConsumerValidationFailedException("Consumer validation failed: " + e.getMessage());
    }
  }
}
