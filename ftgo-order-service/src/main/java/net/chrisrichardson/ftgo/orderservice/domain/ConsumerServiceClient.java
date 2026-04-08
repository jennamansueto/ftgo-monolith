package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceClient.class);

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    try {
      restTemplate.postForEntity(url, new ValidateOrderForConsumerRequest(orderTotal), Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("Consumer not found: {}", consumerId);
        throw new ConsumerNotFoundException(consumerId);
      }
      logger.error("Error calling consumer service: {}", e.getMessage());
      throw new RuntimeException("Failed to validate consumer " + consumerId, e);
    } catch (Exception e) {
      logger.error("Error calling consumer service: {}", e.getMessage());
      throw new RuntimeException("Failed to validate consumer " + consumerId, e);
    }
  }
}
