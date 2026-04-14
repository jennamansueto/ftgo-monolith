package net.chrisrichardson.ftgo.orderservice.proxy;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import org.springframework.http.HttpStatus;
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
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderRequest request = new ValidateOrderRequest(orderTotal);
    try {
      restTemplate.postForEntity(url, request, Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new RuntimeException("Consumer not found: " + consumerId, e);
      }
      throw e;
    }
  }

  public static class ValidateOrderRequest {
    private Money orderTotal;

    private ValidateOrderRequest() {
    }

    public ValidateOrderRequest(Money orderTotal) {
      this.orderTotal = orderTotal;
    }

    public Money getOrderTotal() {
      return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
      this.orderTotal = orderTotal;
    }
  }
}
