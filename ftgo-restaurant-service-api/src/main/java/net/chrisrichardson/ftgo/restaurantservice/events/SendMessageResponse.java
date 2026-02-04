package net.chrisrichardson.ftgo.restaurantservice.events;

import java.time.LocalDateTime;

public class SendMessageResponse {

  private Long messageId;
  private Long orderId;
  private String message;
  private LocalDateTime createdAt;

  public SendMessageResponse() {
  }

  public SendMessageResponse(Long messageId, Long orderId, String message, LocalDateTime createdAt) {
    this.messageId = messageId;
    this.orderId = orderId;
    this.message = message;
    this.createdAt = createdAt;
  }

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
