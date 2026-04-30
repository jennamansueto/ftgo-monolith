package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxy {
    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        restTemplate.postForEntity(
            consumerServiceUrl + "/consumers/" + consumerId + "/validate",
            new ValidateOrderRequest(orderTotal),
            Void.class
        );
    }
}
