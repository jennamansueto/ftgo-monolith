package net.chrisrichardson.ftgo.consumerservice.api.web;

import java.math.BigDecimal;

public class ValidateOrderForConsumerRequest {
  private BigDecimal orderTotal;

  public ValidateOrderForConsumerRequest() {
  }

  public ValidateOrderForConsumerRequest(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }

  public BigDecimal getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }
}
