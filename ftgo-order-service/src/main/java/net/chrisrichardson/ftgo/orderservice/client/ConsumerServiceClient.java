package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    try {
      String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
      ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
      restTemplate.postForEntity(url, request, Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException();
      }
      logger.error("Error validating consumer {}: HTTP {}", consumerId, e.getStatusCode(), e);
      throw new RuntimeException("Consumer service validation failed for consumer " + consumerId, e);
    } catch (RestClientException e) {
      logger.error("Error communicating with consumer service for consumer {}", consumerId, e);
      throw new RuntimeException("Consumer service unavailable", e);
    }
  }
}
