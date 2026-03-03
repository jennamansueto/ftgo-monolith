package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class ConsumerServiceClient {

  private final String consumerServiceUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceClient(String consumerServiceUrl, RestTemplate restTemplate) {
    this.consumerServiceUrl = consumerServiceUrl;
    this.restTemplate = restTemplate;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/{consumerId}/validate";
    try {
      restTemplate.postForEntity(
              url,
              Collections.singletonMap("orderTotal", orderTotal.asString()),
              Void.class,
              consumerId);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException(consumerId);
      }
      throw e;
    }
  }
}
