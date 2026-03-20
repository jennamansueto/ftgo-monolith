package net.chrisrichardson.ftgo.consumerservice.api;

public class ValidateOrderForConsumerRequest {
    private String orderTotal;

    private ValidateOrderForConsumerRequest() {
    }

    public ValidateOrderForConsumerRequest(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }
}
