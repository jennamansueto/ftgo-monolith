package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.api.web.ConsumerVerificationFailedException;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client that replaces the in-process call to the (now extracted) consumer service.
 * It mirrors the original {@code ConsumerService.validateOrderForConsumer} signature so that
 * callers only need to swap the injected collaborator. This class contains HTTP plumbing only;
 * all business rules live in the consumer service.
 */
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
    try {
      restTemplate.postForEntity(url, new ValidateOrderRequest(orderTotal), Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException("Consumer not found: " + consumerId);
      }
      throw new ConsumerVerificationFailedException(
              "Consumer validation failed for consumer " + consumerId + ": " + e.getStatusCode(), e);
    } catch (HttpStatusCodeException e) {
      throw new ConsumerVerificationFailedException(
              "Consumer service returned an error for consumer " + consumerId + ": " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      logger.error("Error calling consumer service at {}", url, e);
      throw new ConsumerVerificationFailedException(
              "Unable to reach consumer service at " + url, e);
    }
  }
}
