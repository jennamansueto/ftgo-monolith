package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import org.springframework.web.client.RestTemplate;

public class RemoteConsumerServiceProxy implements ConsumerServiceProxy {

    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public RemoteConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    @Override
    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        restTemplate.getForObject(consumerServiceUrl + "/consumers/{id}", Object.class, consumerId);
    }
}
