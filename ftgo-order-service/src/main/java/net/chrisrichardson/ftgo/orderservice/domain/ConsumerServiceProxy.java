package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(consumerId, orderTotal);
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(
              consumerServiceUrl + "/consumers/validate-order",
              request,
              Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ConsumerVerificationFailedException();
      }
    } catch (HttpClientErrorException e) {
      throw new ConsumerVerificationFailedException();
    }
  }
}
