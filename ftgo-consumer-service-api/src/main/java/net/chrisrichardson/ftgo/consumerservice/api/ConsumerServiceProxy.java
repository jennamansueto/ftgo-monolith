package net.chrisrichardson.ftgo.consumerservice.api;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
      restTemplate.postForEntity(
              consumerServiceUrl + "/consumers/{consumerId}/validate",
              entity,
              Void.class,
              consumerId);
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Consumer not found or validation failed for consumer " + consumerId, e);
    } catch (HttpServerErrorException e) {
      throw new RuntimeException("Consumer service error while validating consumer " + consumerId, e);
    }
  }
}
