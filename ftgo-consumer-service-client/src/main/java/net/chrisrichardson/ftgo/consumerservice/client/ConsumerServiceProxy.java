package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy implements ConsumerService {

  private final String baseUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceProxy(String baseUrl, RestTemplate restTemplate) {
    this.baseUrl = stripTrailingSlash(baseUrl);
    this.restTemplate = restTemplate;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = baseUrl + "/consumers/" + consumerId + "/validate";
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(
              url,
              new ValidateOrderForConsumerRequest(orderTotal),
              Void.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ConsumerVerificationFailedException("Unexpected response from consumer service: " + response.getStatusCode());
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException(consumerId);
      }
      throw new ConsumerVerificationFailedException("Consumer service rejected request: " + e.getStatusCode(), e);
    }
  }

  private static String stripTrailingSlash(String url) {
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }
}
