package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private RestTemplate restTemplate;
  private String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    logger.info("Validating order for consumer {} via REST call to {}", consumerId, url);
    try {
      HttpEntity<ValidateOrderForConsumerRequest> request =
              new HttpEntity<>(new ValidateOrderForConsumerRequest(orderTotal));
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ConsumerValidationFailedException(consumerId);
      }
    } catch (RestClientException e) {
      logger.error("Consumer validation failed for consumerId={}: {}", consumerId, e.getMessage());
      throw new ConsumerValidationFailedException(consumerId);
    }
  }
}
