package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.orderservice.domain.ConsumerServiceProxy;

public class InProcessConsumerServiceProxy extends ConsumerServiceProxy {

    private final ConsumerService consumerService;

    public InProcessConsumerServiceProxy(ConsumerService consumerService) {
        super(null, null);
        this.consumerService = consumerService;
    }

    @Override
    public void validateOrderForConsumer(long consumerId, Money orderTotal) {
        consumerService.validateOrderForConsumer(consumerId, orderTotal);
    }
}
