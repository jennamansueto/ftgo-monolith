package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class ConsumerServiceProxy implements ConsumerServiceInterface {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    logger.info("Validating order for consumer {} with total {} via {}", consumerId, orderTotal, url);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> body = Collections.singletonMap("orderTotal", orderTotal.asString());
    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        logger.info("Consumer {} validated successfully", consumerId);
        return;
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException("Consumer not found: " + consumerId);
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerVerificationFailedException("Consumer verification failed for consumer: " + consumerId);
      }
      throw new RuntimeException("Unexpected error from consumer service: " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable at {}", consumerServiceUrl, e);
      throw new ConsumerServiceUnavailableException("Consumer service unavailable at " + consumerServiceUrl, e);
    }
  }
}
