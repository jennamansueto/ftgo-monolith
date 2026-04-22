package net.chrisrichardson.ftgo.orderservice.consumerclient;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderByConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceClient.class);

  private final RestTemplate restTemplate;
  private final String consumerServiceBaseUrl;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceBaseUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceBaseUrl = consumerServiceBaseUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceBaseUrl + "/consumers/" + consumerId + "/validate";
    try {
      restTemplate.postForEntity(url, new ValidateOrderByConsumerRequest(orderTotal), Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException();
      }
      logger.error("Consumer service returned HTTP error {} validating consumer {}: {}",
              e.getStatusCode(), consumerId, e.getMessage());
      throw new ConsumerServiceException(
              "Consumer service HTTP error: " + e.getStatusCode(), e);
    } catch (RestClientException e) {
      logger.error("Network error calling consumer service to validate consumer {}: {}",
              consumerId, e.getMessage());
      throw new ConsumerServiceException("Network error calling consumer service", e);
    }
  }
}
