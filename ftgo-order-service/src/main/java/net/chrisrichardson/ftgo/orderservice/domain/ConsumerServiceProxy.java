package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
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
    restTemplate.postForEntity(url, request, Void.class);
  }
}
