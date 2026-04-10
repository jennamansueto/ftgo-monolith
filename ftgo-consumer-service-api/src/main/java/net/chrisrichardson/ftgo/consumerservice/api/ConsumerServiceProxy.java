package net.chrisrichardson.ftgo.consumerservice.api;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerServiceInterface {

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(
              consumerServiceUrl + "/consumers/{consumerId}/validate",
              entity,
              Void.class,
              consumerId);

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException("Consumer validation failed for consumer " + consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RuntimeException("Consumer not found or validation failed for consumer " + consumerId);
      }
      throw e;
    }
  }
}
