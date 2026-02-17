package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
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
      restTemplate.postForEntity(
              consumerServiceUrl + "/consumers/{consumerId}/validate",
              new ValidateOrderForConsumerRequest(orderTotal),
              Void.class,
              consumerId);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("Consumer not found: consumerId={}", consumerId);
        throw new ConsumerNotFoundException(consumerId);
      }
      logger.error("Consumer validation failed: consumerId={}, status={}", consumerId, e.getStatusCode(), e);
      throw new ConsumerValidationFailedException("Consumer validation failed for consumerId=" + consumerId, e);
    } catch (Exception e) {
      logger.error("Error calling consumer service: consumerId={}", consumerId, e);
      throw new ConsumerValidationFailedException("Error calling consumer service for consumerId=" + consumerId, e);
    }
  }
}
