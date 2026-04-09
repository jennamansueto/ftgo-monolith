package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateConsumerRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsumerServiceProxyTest {

  private RestTemplate restTemplate;
  private ConsumerServiceProxy proxy;
  private static final String BASE_URL = "http://localhost:8082";

  @Before
  public void setUp() {
    restTemplate = mock(RestTemplate.class);
    proxy = new ConsumerServiceProxy(restTemplate, BASE_URL);
  }

  @Test
  public void shouldValidateConsumerSuccessfully() {
    long consumerId = 1L;
    Money orderTotal = new Money("100.00");
    String url = BASE_URL + "/consumers/" + consumerId + "/validate";

    when(restTemplate.postForEntity(eq(url), any(ValidateConsumerRequest.class), eq(Void.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    proxy.validateOrderForConsumer(consumerId, orderTotal);
  }

  @Test(expected = ConsumerValidationFailedException.class)
  public void shouldThrowExceptionWhenConsumerNotFound() {
    long consumerId = 999L;
    Money orderTotal = new Money("100.00");
    String url = BASE_URL + "/consumers/" + consumerId + "/validate";

    when(restTemplate.postForEntity(eq(url), any(ValidateConsumerRequest.class), eq(Void.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    proxy.validateOrderForConsumer(consumerId, orderTotal);
  }
}
