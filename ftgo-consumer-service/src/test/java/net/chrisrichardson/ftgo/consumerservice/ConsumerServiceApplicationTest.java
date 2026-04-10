package net.chrisrichardson.ftgo.consumerservice;

import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConsumerServiceApplicationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void shouldCreateConsumer() {
    CreateConsumerRequest request = new CreateConsumerRequest(new PersonName("John", "Doe"));
    ResponseEntity<CreateConsumerResponse> response =
            restTemplate.postForEntity("/consumers", request, CreateConsumerResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getConsumerId() > 0);
  }

  @Test
  public void shouldGetConsumer() {
    CreateConsumerRequest createRequest = new CreateConsumerRequest(new PersonName("Jane", "Doe"));
    ResponseEntity<CreateConsumerResponse> createResponse =
            restTemplate.postForEntity("/consumers", createRequest, CreateConsumerResponse.class);

    long consumerId = createResponse.getBody().getConsumerId();

    ResponseEntity<String> getResponse =
            restTemplate.getForEntity("/consumers/" + consumerId, String.class);

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }

  @Test
  public void shouldValidateConsumer() {
    CreateConsumerRequest createRequest = new CreateConsumerRequest(new PersonName("Bob", "Smith"));
    ResponseEntity<CreateConsumerResponse> createResponse =
            restTemplate.postForEntity("/consumers", createRequest, CreateConsumerResponse.class);

    long consumerId = createResponse.getBody().getConsumerId();

    ValidateOrderForConsumerRequest validateRequest = new ValidateOrderForConsumerRequest(new Money("100.00"));
    HttpEntity<ValidateOrderForConsumerRequest> entity = new HttpEntity<>(validateRequest);

    ResponseEntity<Void> validateResponse =
            restTemplate.postForEntity("/consumers/" + consumerId + "/validate", entity, Void.class);

    assertEquals(HttpStatus.OK, validateResponse.getStatusCode());
  }

  @Test
  public void shouldReturnNotFoundForNonExistentConsumer() {
    ResponseEntity<String> response =
            restTemplate.getForEntity("/consumers/999999", String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
