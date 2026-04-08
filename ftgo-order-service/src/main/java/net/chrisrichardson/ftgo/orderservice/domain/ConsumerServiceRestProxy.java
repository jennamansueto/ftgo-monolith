package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceRestProxy implements ConsumerServiceProxy {
    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceRestProxy(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    @Override
    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        // Call the consumer service REST endpoint to validate
        restTemplate.getForObject(consumerServiceUrl + "/consumers/" + consumerId, String.class);
    }
}
