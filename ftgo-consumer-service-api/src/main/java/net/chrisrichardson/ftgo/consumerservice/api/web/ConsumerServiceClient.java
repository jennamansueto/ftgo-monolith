package net.chrisrichardson.ftgo.consumerservice.api.web;

import net.chrisrichardson.ftgo.common.Money;
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

public class ConsumerServiceClient {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  private int maxRetries = 3;
  private long retryDelayMs = 500;
  private long circuitBreakerThreshold = 5;
  private long circuitBreakerResetTimeMs = 30000;

  private long failureCount = 0;
  private long lastFailureTime = 0;
  private boolean circuitOpen = false;

  public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    if (isCircuitOpen()) {
      logger.warn("Circuit breaker is open, failing fast for consumer validation consumerId={}", consumerId);
      throw new ConsumerValidationUnavailableException("Consumer service is unavailable");
    }

    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate-order";
    ValidateOrderRequest request = new ValidateOrderRequest(new java.math.BigDecimal(orderTotal.asString()));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateOrderRequest> entity = new HttpEntity<>(request, headers);

    int attempt = 0;
    while (true) {
      try {
        ResponseEntity<ValidateOrderResponse> response = restTemplate.postForEntity(
                url, entity, ValidateOrderResponse.class);

        resetCircuitBreaker();

        if (response.getStatusCode() == HttpStatus.OK) {
          ValidateOrderResponse body = response.getBody();
          if (body != null && !body.isValid()) {
            throw new ConsumerValidationFailedException(body.getMessage());
          }
          return;
        }
      } catch (HttpClientErrorException e) {
        resetCircuitBreaker();
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
          throw new ConsumerNotFoundException("Consumer not found: " + consumerId);
        }
        if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
          throw new ConsumerValidationFailedException("Consumer validation failed for consumer " + consumerId);
        }
        throw new ConsumerValidationFailedException("Consumer validation error: " + e.getMessage());
      } catch (ResourceAccessException e) {
        attempt++;
        recordFailure();
        if (attempt >= maxRetries) {
          logger.error("Failed to validate consumer after {} attempts, consumerId={}", maxRetries, consumerId, e);
          throw new ConsumerValidationUnavailableException("Consumer service unavailable after " + maxRetries + " retries", e);
        }
        long delay = retryDelayMs * (1L << (attempt - 1));
        logger.warn("Consumer service call failed, retrying in {}ms (attempt {}/{}), consumerId={}", delay, attempt, maxRetries, consumerId);
        try {
          Thread.sleep(delay);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new ConsumerValidationUnavailableException("Interrupted during retry", ie);
        }
      }
    }
  }

  private synchronized boolean isCircuitOpen() {
    if (!circuitOpen) {
      return false;
    }
    if (System.currentTimeMillis() - lastFailureTime > circuitBreakerResetTimeMs) {
      logger.info("Circuit breaker half-open, allowing next request through");
      circuitOpen = false;
      failureCount = 0;
      return false;
    }
    return true;
  }

  private synchronized void recordFailure() {
    failureCount++;
    lastFailureTime = System.currentTimeMillis();
    if (failureCount >= circuitBreakerThreshold) {
      circuitOpen = true;
      logger.warn("Circuit breaker opened after {} failures", failureCount);
    }
  }

  private synchronized void resetCircuitBreaker() {
    failureCount = 0;
    circuitOpen = false;
  }

  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  public void setRetryDelayMs(long retryDelayMs) {
    this.retryDelayMs = retryDelayMs;
  }

  public void setCircuitBreakerThreshold(long circuitBreakerThreshold) {
    this.circuitBreakerThreshold = circuitBreakerThreshold;
  }

  public void setCircuitBreakerResetTimeMs(long circuitBreakerResetTimeMs) {
    this.circuitBreakerResetTimeMs = circuitBreakerResetTimeMs;
  }
}
