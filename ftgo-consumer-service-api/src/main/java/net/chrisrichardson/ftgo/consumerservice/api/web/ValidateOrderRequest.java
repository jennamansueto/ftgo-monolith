package net.chrisrichardson.ftgo.consumerservice.api.web;

import java.math.BigDecimal;

public class ValidateOrderRequest {
  private BigDecimal orderTotal;

  public ValidateOrderRequest() {
  }

  public ValidateOrderRequest(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }

  public BigDecimal getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }
}
