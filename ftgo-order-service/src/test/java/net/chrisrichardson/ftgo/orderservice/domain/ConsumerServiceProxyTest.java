package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class ConsumerServiceProxyTest {

  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;
  private ConsumerServiceProxy proxy;

  private static final String CONSUMER_SERVICE_URL = "http://localhost:8082";
  private static final long CONSUMER_ID = 1L;
  private static final Money ORDER_TOTAL = new Money("12.34");

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    proxy = new ConsumerServiceProxy(restTemplate, CONSUMER_SERVICE_URL);
  }

  @Test
  public void shouldValidateOrderForConsumer() {
    mockServer.expect(requestTo(CONSUMER_SERVICE_URL + "/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess());

    proxy.validateOrderForConsumer(CONSUMER_ID, ORDER_TOTAL);

    mockServer.verify();
  }

  @Test(expected = ConsumerNotFoundException.class)
  public void shouldThrowConsumerNotFoundExceptionWhen404() {
    mockServer.expect(requestTo(CONSUMER_SERVICE_URL + "/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

    proxy.validateOrderForConsumer(CONSUMER_ID, ORDER_TOTAL);
  }

  @Test(expected = ConsumerVerificationFailedException.class)
  public void shouldThrowConsumerVerificationFailedExceptionWhen422() {
    mockServer.expect(requestTo(CONSUMER_SERVICE_URL + "/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

    proxy.validateOrderForConsumer(CONSUMER_ID, ORDER_TOTAL);
  }
}
