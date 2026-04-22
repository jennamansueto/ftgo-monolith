package net.chrisrichardson.ftgo.consumerservice.client;

import net.chrisrichardson.ftgo.common.Money;

public interface ConsumerService {

  void validateOrderForConsumer(long consumerId, Money orderTotal);
}
