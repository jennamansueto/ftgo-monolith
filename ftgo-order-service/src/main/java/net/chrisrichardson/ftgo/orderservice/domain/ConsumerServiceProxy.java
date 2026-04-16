package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ConsumerValidationFailedException(consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerValidationFailedException(consumerId);
      }
      throw e;
    }
  }
}
