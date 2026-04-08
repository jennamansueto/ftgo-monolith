package net.chrisrichardson.ftgo.consumerservice.api.proxy;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    String url = consumerServiceUrl + "/consumers/{consumerId}/validate";
    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class, consumerId);
      if (response.getStatusCode() == HttpStatus.OK) {
        return;
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException("Consumer not found: " + consumerId);
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerVerificationException("Consumer verification failed for consumer: " + consumerId);
      }
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (HttpServerErrorException e) {
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      throw new ConsumerServiceUnavailableException("Consumer service is unavailable", e);
    }
  }

  public long create(PersonName name) {
    String url = consumerServiceUrl + "/consumers";
    CreateConsumerRequest request = new CreateConsumerRequest(name);

    try {
      CreateConsumerResponse response = restTemplate.postForObject(url, request, CreateConsumerResponse.class);
      if (response == null) {
        throw new ConsumerServiceUnavailableException("Consumer service returned empty response");
      }
      return response.getConsumerId();
    } catch (HttpServerErrorException e) {
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      throw new ConsumerServiceUnavailableException("Consumer service is unavailable", e);
    }
  }

  public CreateConsumerResponse findById(long consumerId) {
    String url = consumerServiceUrl + "/consumers/{consumerId}";

    try {
      ResponseEntity<CreateConsumerResponse> response = restTemplate.getForEntity(url, CreateConsumerResponse.class, consumerId);
      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody();
      }
      return null;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return null;
      }
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (HttpServerErrorException e) {
      throw new ConsumerServiceUnavailableException("Consumer service returned error: " + e.getStatusCode(), e);
    } catch (ResourceAccessException e) {
      throw new ConsumerServiceUnavailableException("Consumer service is unavailable", e);
    }
  }
}
