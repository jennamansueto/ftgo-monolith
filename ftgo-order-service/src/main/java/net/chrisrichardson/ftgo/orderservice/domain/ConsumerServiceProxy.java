package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateConsumerRequest;
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
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, new ValidateConsumerRequest(orderTotal), Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ConsumerValidationFailedException(consumerId);
      }
    } catch (HttpClientErrorException e) {
      throw new ConsumerValidationFailedException(consumerId);
    }
  }
}
