package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.common.Money;
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
        restTemplate.postForEntity(
            consumerServiceUrl + "/consumers/" + consumerId + "/validate",
            new ValidateOrderRequest(orderTotal),
            Void.class);
    }
}
