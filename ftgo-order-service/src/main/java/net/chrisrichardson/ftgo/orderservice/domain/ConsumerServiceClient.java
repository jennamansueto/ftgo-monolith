package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerVerificationFailedException;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ConsumerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceClient.class);
    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;

    public ConsumerServiceClient(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;
    }

    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        try {
            restTemplate.postForEntity(
                consumerServiceUrl + "/consumers/{consumerId}/validate",
                new ValidateOrderRequest(orderTotal),
                Void.class,
                consumerId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ConsumerNotFoundException();
            }
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ConsumerVerificationFailedException();
            }
            logger.error("Error calling consumer service", e);
            throw new RuntimeException("Consumer service call failed", e);
        }
    }
}
