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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

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
    @Primary
    public CourierServiceClient testCourierServiceClient(Environment env) {
      String port = env.getProperty("local.server.port", "8084");
      return new CourierServiceClient(new RestTemplate(), "http://localhost:" + port);
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
