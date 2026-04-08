package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {

    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        restTemplate.getForObject(
            consumerServiceUrl + "/consumers/" + consumerId,
            Object.class);
    }
}
