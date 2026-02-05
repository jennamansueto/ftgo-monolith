package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(
            orderTotal.asString() != null ? new java.math.BigDecimal(orderTotal.asString()) : java.math.BigDecimal.ZERO
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request, headers);

    try {
      logger.info("Validating order for consumer {} with total {}", consumerId, orderTotal);
      ResponseEntity<ValidateOrderForConsumerResponse> response = restTemplate.postForEntity(
              url, entity, ValidateOrderForConsumerResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        if (!response.getBody().isValid()) {
          throw new ConsumerValidationException("Consumer validation failed: " + response.getBody().getMessage());
        }
        logger.info("Consumer {} validated successfully", consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.error("Consumer {} not found", consumerId);
        throw new ConsumerNotFoundException(consumerId);
      } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
        logger.error("Consumer validation failed for consumer {}", consumerId);
        throw new ConsumerValidationException("Consumer validation failed");
      }
      logger.error("HTTP error communicating with Consumer Service: {}", e.getMessage());
      throw new ConsumerServiceUnavailableException("Consumer Service error", e);
    } catch (RestClientException e) {
      logger.error("Error communicating with Consumer Service: {}", e.getMessage());
      throw new ConsumerServiceUnavailableException("Consumer Service is unavailable", e);
    }
  }
}
