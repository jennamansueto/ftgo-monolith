package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.orderservice.domain.ConsumerServiceProxy;

/**
 * Test-only proxy that delegates directly to ConsumerService in-process,
 * bypassing the HTTP call to avoid random port mismatch in integration tests.
 */
public class InProcessConsumerServiceProxy extends ConsumerServiceProxy {

  private final ConsumerService consumerService;

  public InProcessConsumerServiceProxy(ConsumerService consumerService) {
    super(null, null);
    this.consumerService = consumerService;
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    try {
      consumerService.validateOrderForConsumer(consumerId, orderTotal);
    } catch (RuntimeException e) {
      throw new net.chrisrichardson.ftgo.orderservice.domain.ConsumerValidationFailedException(consumerId);
    }
  }
}
