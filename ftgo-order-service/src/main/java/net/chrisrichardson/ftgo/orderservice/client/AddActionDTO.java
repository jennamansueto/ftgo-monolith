package net.chrisrichardson.ftgo.orderservice.client;

import java.time.LocalDateTime;

public class AddActionDTO {

  private String type;
  private Long orderId;
  private LocalDateTime time;

  private AddActionDTO() {
  }

  public AddActionDTO(String type, Long orderId, LocalDateTime time) {
    this.type = type;
    this.orderId = orderId;
    this.time = time;
  }

  public String getType() {
    return type;
  }

  public Long getOrderId() {
    return orderId;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }
}
