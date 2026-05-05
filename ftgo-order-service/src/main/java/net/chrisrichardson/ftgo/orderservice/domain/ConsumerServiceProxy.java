package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceAPI;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerVerificationFailedException;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP-based client for the extracted ftgo-consumer-service microservice.
 *
 * <p>This replaces the in-process {@code ConsumerService} bean that the order
 * service used to inject directly. It calls the consumer service's REST API
 * and translates HTTP status codes back into the same domain exceptions the
 * order service used to see.
 */
public class ConsumerServiceProxy implements ConsumerServiceAPI {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceProxy.class);

  private final String baseUrl;
  private final RestTemplate restTemplate;

  public ConsumerServiceProxy(String baseUrl, RestTemplate restTemplate) {
    this.baseUrl = stripTrailingSlash(baseUrl);
    this.restTemplate = restTemplate;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = baseUrl + "/consumers/" + consumerId + "/validate";
    logger.debug("Validating order for consumer {} via {}", consumerId, url);
    try {
      restTemplate.postForEntity(url, new ValidateOrderForConsumerRequest(orderTotal), Void.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerNotFoundException();
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ConsumerVerificationFailedException();
      }
      throw e;
    }
  }

  private static String stripTrailingSlash(String url) {
    if (url == null) {
      return null;
    }
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }
}
