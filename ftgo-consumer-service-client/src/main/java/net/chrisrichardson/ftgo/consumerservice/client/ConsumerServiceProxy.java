package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerValidationService;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerValidationService {

  private String consumerServiceUrl;
  private RestTemplate restTemplate;

  public ConsumerServiceProxy(String consumerServiceUrl, RestTemplate restTemplate) {
    this.consumerServiceUrl = consumerServiceUrl;
    this.restTemplate = restTemplate;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new RuntimeException("Consumer validation failed for consumerId: " + consumerId);
      }
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Consumer validation failed for consumerId: " + consumerId, e);
    }
  }
}
