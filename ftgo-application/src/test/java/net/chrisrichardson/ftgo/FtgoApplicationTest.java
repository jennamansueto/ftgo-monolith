package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.endtoendtests.common.AbstractEndToEndTests;
import net.chrisrichardson.ftgo.orderservice.client.ConsumerServiceClient;
import net.chrisrichardson.ftgo.orderservice.main.OrderServiceConfiguration;
import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=FtgoApplicationTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"consumer.service.url=http://localhost:0"})
public class FtgoApplicationTest extends AbstractEndToEndTests {

  @Configuration
  @EnableAutoConfiguration
  @ComponentScan
  @Import({OrderServiceConfiguration.class,
          RestaurantServiceConfiguration.class})
  public static class Config {

    /**
     * Override ConsumerServiceClient with an in-process no-op adapter that
     * always succeeds validation, bypassing HTTP for the in-process test.
     * The actual consumer creation is handled by the TestConsumerController below.
     */
    @Bean
    @Primary
    public ConsumerServiceClient testConsumerServiceClient() {
      return new ConsumerServiceClient(null, "unused") {
        @Override
        public void validateOrderForConsumer(long consumerId, Money orderTotal) {
          // No-op: always succeeds in the in-process test
        }
      };
    }
  }

  /**
   * Test-only controller that handles consumer REST endpoints in the in-process test.
   * This replaces the extracted consumer service for testing purposes.
   */
  @RestController
  @RequestMapping(path = "/consumers")
  public static class TestConsumerController {

    private final AtomicLong consumerIdSequence = new AtomicLong(1);

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CreateConsumerResponse> create(@RequestBody CreateConsumerRequest request) {
      long id = consumerIdSequence.getAndIncrement();
      return new ResponseEntity<>(new CreateConsumerResponse(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/{consumerId}/validate", method = RequestMethod.POST)
    public ResponseEntity<Void> validate(@PathVariable long consumerId,
                                         @RequestBody ValidateOrderForConsumerRequest request) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
  }

  @LocalServerPort
  private int port;

  @Override
  public String getHost() {
    return "localhost";
  }

  @Override
  public int getApplicationPort() {
    return port;
  }
}
