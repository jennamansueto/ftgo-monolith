package net.chrisrichardson.ftgo.consumerservice.microservice.web;

import net.chrisrichardson.ftgo.common.Money;

public class ValidateOrderRequest {
  private Money orderTotal;

  public Money getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Money orderTotal) {
    this.orderTotal = orderTotal;
  }

  public ValidateOrderRequest() {
  }

  public ValidateOrderRequest(Money orderTotal) {
    this.orderTotal = orderTotal;
  }
}
