package net.chrisrichardson.ftgo.courierservice;

import net.chrisrichardson.ftgo.courierservice.web.CourierWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourierServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(CourierWebConfiguration.class, args);
    }
}
