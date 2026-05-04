package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.consumerservice.main.ConsumerServiceConfiguration;
import net.chrisrichardson.ftgo.courierservice.web.CourierWebConfiguration;
import net.chrisrichardson.ftgo.endtoendtests.common.AbstractEndToEndTests;
import net.chrisrichardson.ftgo.orderservice.domain.CourierServiceClient;
import net.chrisrichardson.ftgo.orderservice.main.OrderServiceConfiguration;
import net.chrisrichardson.ftgo.restaurantservice.RestaurantServiceConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=FtgoApplicationTest.Config.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "courier.service.url=http://localhost:0")
public class FtgoApplicationTest extends AbstractEndToEndTests {

  @Configuration
  @EnableAutoConfiguration
  @ComponentScan
  @Import({ConsumerServiceConfiguration.class,
          OrderServiceConfiguration.class,
          RestaurantServiceConfiguration.class,
          CourierWebConfiguration.class})
  public static class Config {

    @Bean
    public ApplicationListener<ServletWebServerInitializedEvent> courierUrlUpdater(CourierServiceClient courierServiceClient) {
      return event -> {
        int port = event.getWebServer().getPort();
        courierServiceClient.setCourierServiceUrl("http://localhost:" + port);
      };
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

  @Override
  public int getCourierServicePort() {
    return port;
  }
}
