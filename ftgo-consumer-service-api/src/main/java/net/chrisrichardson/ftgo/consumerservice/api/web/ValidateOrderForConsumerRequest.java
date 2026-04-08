package net.chrisrichardson.ftgo.consumerservice.api.web;

import net.chrisrichardson.ftgo.common.Money;

public class ValidateOrderForConsumerRequest {
  private Money orderTotal;

  private ValidateOrderForConsumerRequest() {
  }

  public ValidateOrderForConsumerRequest(Money orderTotal) {
    this.orderTotal = orderTotal;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }
}
