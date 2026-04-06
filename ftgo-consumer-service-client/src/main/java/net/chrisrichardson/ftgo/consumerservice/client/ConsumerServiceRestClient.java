package net.chrisrichardson.ftgo.consumerservice.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceProxy;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public class ConsumerServiceRestClient implements ConsumerServiceProxy {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;
    private final String consumerServiceUrl;
    private final CircuitBreaker circuitBreaker;

    public ConsumerServiceRestClient(RestTemplate restTemplate, String consumerServiceUrl) {
        this.restTemplate = restTemplate;
        this.consumerServiceUrl = consumerServiceUrl;

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(10000))
                .ringBufferSizeInClosedState(10)
                .ringBufferSizeInHalfOpenState(5)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.circuitBreaker = registry.circuitBreaker("consumerService");
    }

    @Override
    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        try {
            CircuitBreaker.decorateRunnable(circuitBreaker, () -> {
                String url = consumerServiceUrl + "/consumers/" + consumerId + "/validate";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ValidateOrderRequest> request = new HttpEntity<>(new ValidateOrderRequest(orderTotal), headers);
                restTemplate.postForEntity(url, request, Void.class);
            }).run();
        } catch (HttpClientErrorException e) {
            logger.error("Consumer validation failed for consumerId={}: {}", consumerId, e.getStatusCode());
            throw new ConsumerValidationException("Consumer validation failed for consumerId=" + consumerId, e);
        } catch (Exception e) {
            logger.error("Error calling consumer service for consumerId={}: {}", consumerId, e.getMessage());
            throw new ConsumerValidationException("Error calling consumer service for consumerId=" + consumerId, e);
        }
    }
}
