package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerValidationService;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerValidationService {

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request, headers);
    try {
      restTemplate.postForEntity(url, entity, Void.class);
    } catch (RestClientException e) {
      throw new ConsumerValidationFailedException(consumerId);
    }
  }
}
