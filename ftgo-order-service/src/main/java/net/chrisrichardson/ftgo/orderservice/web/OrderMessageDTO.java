package net.chrisrichardson.ftgo.orderservice.web;

import java.time.LocalDateTime;

public class OrderMessageDTO {

  private Long id;
  private Long orderId;
  private String message;
  private LocalDateTime sentAt;

  public OrderMessageDTO() {
  }

  public OrderMessageDTO(Long id, Long orderId, String message, LocalDateTime sentAt) {
    this.id = id;
    this.orderId = orderId;
    this.message = message;
    this.sentAt = sentAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
  }
}
