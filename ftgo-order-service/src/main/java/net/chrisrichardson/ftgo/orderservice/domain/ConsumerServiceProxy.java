package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerServiceClient {

  private final String consumerServiceUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceProxy(String consumerServiceUrl, RestTemplate restTemplate) {
    this.consumerServiceUrl = consumerServiceUrl;
    this.restTemplate = restTemplate;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderRequest request = new ValidateOrderRequest(orderTotal);
    try {
      restTemplate.postForEntity(url, request, Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerValidationException("Consumer not found: " + consumerId, e);
      } else if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerValidationException("Order validation failed for consumer " + consumerId, e);
      }
      throw new ConsumerValidationException("Consumer service error for consumer " + consumerId + ": " + e.getStatusCode(), e);
    }
  }
}
