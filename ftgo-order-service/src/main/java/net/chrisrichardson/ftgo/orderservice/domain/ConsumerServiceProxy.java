package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
    String url = String.format("%s/consumers/%d/validate", consumerServiceUrl, consumerId);

    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new ConsumerVerificationFailedException();
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException(consumerId);
      }
      logger.error("Consumer validation failed for consumerId={}: {}", consumerId, e.getMessage());
      throw new ConsumerVerificationFailedException();
    } catch (HttpServerErrorException e) {
      logger.error("Consumer service error for consumerId={}: {}", consumerId, e.getMessage());
      throw new ConsumerServiceUnavailableException(e);
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable: {}", e.getMessage());
      throw new ConsumerServiceUnavailableException(e);
    }
  }
}
