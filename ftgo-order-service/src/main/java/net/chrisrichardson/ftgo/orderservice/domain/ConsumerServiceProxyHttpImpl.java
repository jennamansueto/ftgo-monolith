package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxyHttpImpl implements ConsumerServiceProxy {

  private final String consumerServiceUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceProxyHttpImpl(String consumerServiceUrl, RestTemplate restTemplate) {
    this.consumerServiceUrl = consumerServiceUrl;
    this.restTemplate = restTemplate;
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
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException(consumerId);
      }
      throw new ConsumerValidationFailedException(consumerId, e);
    }
  }
}
