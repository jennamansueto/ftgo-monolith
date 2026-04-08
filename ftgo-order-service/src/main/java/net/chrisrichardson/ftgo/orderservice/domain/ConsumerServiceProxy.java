package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class ConsumerServiceProxy {

    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceProxy(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        Map<String, Money> request = Collections.singletonMap("orderTotal", orderTotal);
        restTemplate.postForObject(
            consumerServiceUrl + "/consumers/" + consumerId + "/validate-order",
            request,
            Void.class);
    }
}
