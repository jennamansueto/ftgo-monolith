package net.chrisrichardson.ftgo.consumerservice.api.proxy;

import net.chrisrichardson.ftgo.common.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ConsumerServiceProxyTest {

  private ConsumerServiceProxy proxy;
  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    RestTemplate restTemplate = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    proxy = new ConsumerServiceProxy(restTemplate, "http://localhost:8082");
  }

  @Test
  public void shouldValidateConsumerSuccessfully() {
    mockServer.expect(requestTo("http://localhost:8082/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess());

    proxy.validateOrderForConsumer(1L, new Money("100.00"));

    mockServer.verify();
  }

  @Test(expected = ConsumerNotFoundException.class)
  public void shouldThrowNotFoundWhenConsumerDoesNotExist() {
    mockServer.expect(requestTo("http://localhost:8082/consumers/999/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

    proxy.validateOrderForConsumer(999L, new Money("100.00"));
  }

  @Test(expected = ConsumerVerificationException.class)
  public void shouldThrowVerificationExceptionWhenValidationFails() {
    mockServer.expect(requestTo("http://localhost:8082/consumers/1/validate"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

    proxy.validateOrderForConsumer(1L, new Money("100.00"));
  }

  @Test
  public void shouldCreateConsumerSuccessfully() {
    mockServer.expect(requestTo("http://localhost:8082/consumers"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{\"consumerId\": 42}", MediaType.APPLICATION_JSON));

    long consumerId = proxy.create(new net.chrisrichardson.ftgo.common.PersonName("John", "Doe"));

    assertEquals(42L, consumerId);
    mockServer.verify();
  }

  @Test
  public void shouldReturnNullWhenConsumerNotFoundOnGet() {
    mockServer.expect(requestTo("http://localhost:8082/consumers/999"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

    assertNull(proxy.findById(999L));
    mockServer.verify();
  }
}
