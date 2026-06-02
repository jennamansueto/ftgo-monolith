package net.chrisrichardson.ftgo.courierservice.api;

import java.time.LocalDateTime;

public class ScheduleDeliveryRequest {

  private long orderId;
  private LocalDateTime dropoffTime;

  public ScheduleDeliveryRequest() {
  }

  public ScheduleDeliveryRequest(long orderId, LocalDateTime dropoffTime) {
    this.orderId = orderId;
    this.dropoffTime = dropoffTime;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public LocalDateTime getDropoffTime() {
    return dropoffTime;
  }

  public void setDropoffTime(LocalDateTime dropoffTime) {
    this.dropoffTime = dropoffTime;
  }
}
