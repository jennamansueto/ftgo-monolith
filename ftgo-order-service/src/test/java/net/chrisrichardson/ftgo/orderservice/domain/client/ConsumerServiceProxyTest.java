package net.chrisrichardson.ftgo.orderservice.domain.client;

import net.chrisrichardson.ftgo.common.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class ConsumerServiceProxyTest {

  private ConsumerServiceProxy proxy;
  private MockRestServiceServer mockServer;
  private RestTemplate restTemplate;
  private String baseUrl = "http://localhost:8082";

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    proxy = new ConsumerServiceProxy(restTemplate, baseUrl);
  }

  @Test
  public void shouldValidateConsumerSuccessfully() {
    mockServer.expect(requestTo(baseUrl + "/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK));

    proxy.validateOrderForConsumer(1L, new Money(100));
    mockServer.verify();
  }

  @Test(expected = ConsumerValidationException.class)
  public void shouldThrowExceptionWhenConsumerNotFound() {
    mockServer.expect(requestTo(baseUrl + "/consumers/999/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

    proxy.validateOrderForConsumer(999L, new Money(100));
  }

  @Test(expected = ConsumerValidationException.class)
  public void shouldThrowExceptionWhenValidationFails() {
    mockServer.expect(requestTo(baseUrl + "/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

    proxy.validateOrderForConsumer(1L, new Money(100));
  }
}
