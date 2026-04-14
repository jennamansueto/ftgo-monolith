package net.chrisrichardson.ftgo.orderservice.proxy;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    ValidateConsumerRequest request = new ValidateConsumerRequest(orderTotal);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        return;
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException("Consumer not found: " + consumerId);
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerVerificationFailedException("Consumer verification failed for consumer: " + consumerId);
      }
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      throw new ConsumerServiceUnavailableException("Consumer service is unavailable", e);
    }
  }
}
