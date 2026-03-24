package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate,
                              @Value("${consumer.service.url:http://localhost:8080}") String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    try {
      String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
      ValidateOrderRequest request = new ValidateOrderRequest(orderTotal);
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ConsumerValidationFailedException(consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerValidationFailedException(consumerId);
      }
      throw new ConsumerValidationFailedException(consumerId);
    }
  }
}
