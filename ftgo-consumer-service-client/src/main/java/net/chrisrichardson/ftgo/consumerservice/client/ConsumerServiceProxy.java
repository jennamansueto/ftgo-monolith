package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private final String consumerServiceUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceProxy(String consumerServiceUrl, RestTemplate restTemplate) {
    this.consumerServiceUrl = consumerServiceUrl;
    this.restTemplate = restTemplate;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(
              consumerServiceUrl + "/consumers/{consumerId}/validate",
              new ValidateOrderForConsumerRequest(orderTotal),
              Void.class,
              consumerId);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException(consumerId);
      }
      if (e.getStatusCode() == HttpStatus.CONFLICT) {
        throw new ConsumerVerificationFailedException(consumerId);
      }
      throw e;
    }
  }
}
