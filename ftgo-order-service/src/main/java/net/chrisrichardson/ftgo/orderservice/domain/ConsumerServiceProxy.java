package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class ConsumerServiceProxy {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
    logger.info("Validating order for consumer {} via REST: {}", consumerId, url);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    Map<String, String> orderTotalMap = new HashMap<>();
    orderTotalMap.put("amount", orderTotal.asString());
    body.put("orderTotal", orderTotalMap);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ConsumerValidationFailedException(consumerId);
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ConsumerValidationFailedException(consumerId);
      }
      throw new ConsumerValidationFailedException(consumerId);
    }
  }
}
