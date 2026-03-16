package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    logger.info("Validating order for consumer {} via {}", consumerId, url);
    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(
              url,
              new ValidateOrderForConsumerRequest(orderTotal),
              Void.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        logger.info("Consumer {} validated successfully", consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("Consumer {} not found", consumerId);
        throw new ConsumerNotFoundException();
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        logger.warn("Consumer {} validation failed", consumerId);
        throw new ConsumerVerificationFailedException();
      }
      logger.error("Unexpected error validating consumer {}: {}", consumerId, e.getMessage());
      throw new RuntimeException("Failed to validate consumer " + consumerId, e);
    } catch (Exception e) {
      logger.error("Error communicating with consumer service for consumer {}: {}", consumerId, e.getMessage());
      throw new RuntimeException("Failed to communicate with consumer service", e);
    }
  }
}
