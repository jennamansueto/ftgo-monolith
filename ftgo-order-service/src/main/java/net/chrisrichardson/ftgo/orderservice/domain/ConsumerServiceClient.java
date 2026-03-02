package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {
    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceClient(RestTemplate restTemplate, @Value("${consumer.service.url}") String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(orderTotal);
        try {
            restTemplate.postForEntity(
                    consumerServiceUrl + "/consumers/" + consumerId + "/validate",
                    request,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ConsumerValidationFailedException(consumerId);
            }
            throw e;
        }
    }
}
