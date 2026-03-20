package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.common.Money;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceProxyHttpClient implements ConsumerServiceProxy {
    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceProxyHttpClient(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    @Override
    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal.asString());
        restTemplate.postForEntity(
            consumerServiceUrl + "/consumers/" + consumerId + "/validate",
            request,
            Void.class
        );
    }
}
