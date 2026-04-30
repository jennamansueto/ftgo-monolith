package net.chrisrichardson.ftgo.consumerservice.api;

import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.GetConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RequestMapping(path = "/consumers")
@ResponseBody
public class ConsumerApiProxyController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final String consumerServiceUrl;

  public ConsumerApiProxyController(RestTemplate restTemplate, String consumerServiceUrl) {
    this.restTemplate = restTemplate;
    this.consumerServiceUrl = consumerServiceUrl;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CreateConsumerResponse> create(@RequestBody CreateConsumerRequest request) {
    String url = consumerServiceUrl + "/consumers";
    try {
      CreateConsumerResponse response = restTemplate.postForObject(url, request, CreateConsumerResponse.class);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      return new ResponseEntity<>(e.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable at {}", consumerServiceUrl, e);
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    String url = String.format("%s/consumers/%d", consumerServiceUrl, consumerId);
    try {
      GetConsumerResponse response = restTemplate.getForObject(url, GetConsumerResponse.class);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(e.getStatusCode());
    } catch (HttpServerErrorException e) {
      return new ResponseEntity<>(e.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable at {}", consumerServiceUrl, e);
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<Void> validateOrderForConsumer(@PathVariable long consumerId,
                                                       @RequestBody ValidateOrderForConsumerRequest request) {
    String url = String.format("%s/consumers/%d/validate", consumerServiceUrl, consumerId);
    try {
      restTemplate.postForEntity(url, request, Void.class);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
      }
      return new ResponseEntity<>(e.getStatusCode());
    } catch (HttpServerErrorException e) {
      return new ResponseEntity<>(e.getStatusCode());
    } catch (ResourceAccessException e) {
      logger.error("Consumer service unavailable at {}", consumerServiceUrl, e);
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}
