package net.chrisrichardson.ftgo.consumerservice.api.web;

import net.chrisrichardson.ftgo.common.Money;

public class ValidateOrderRequest {
  private Money orderTotal;

  public Money getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Money orderTotal) {
    this.orderTotal = orderTotal;
  }

  public ValidateOrderRequest(Money orderTotal) {
    this.orderTotal = orderTotal;
  }

  private ValidateOrderRequest() {
  }
}
