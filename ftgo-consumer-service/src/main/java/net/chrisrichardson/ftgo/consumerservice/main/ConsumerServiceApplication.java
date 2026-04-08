package net.chrisrichardson.ftgo.consumerservice.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import net.chrisrichardson.ftgo.consumerservice.web.ConsumerWebConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = "net.chrisrichardson.ftgo.consumerservice")
@EntityScan(basePackages = "net.chrisrichardson.ftgo.consumerservice.domain")
public class ConsumerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerServiceApplication.class, args);
    }
}
