package net.chrisrichardson.ftgo.orderservice.api.web;

import java.time.LocalDateTime;

public class UpdateEtaRequest {
  private LocalDateTime estimatedPickupTime;
  private LocalDateTime estimatedDeliveryTime;

  public UpdateEtaRequest() {
  }

  public UpdateEtaRequest(LocalDateTime estimatedPickupTime, LocalDateTime estimatedDeliveryTime) {
    this.estimatedPickupTime = estimatedPickupTime;
    this.estimatedDeliveryTime = estimatedDeliveryTime;
  }

  public LocalDateTime getEstimatedPickupTime() {
    return estimatedPickupTime;
  }

  public void setEstimatedPickupTime(LocalDateTime estimatedPickupTime) {
    this.estimatedPickupTime = estimatedPickupTime;
  }

  public LocalDateTime getEstimatedDeliveryTime() {
    return estimatedDeliveryTime;
  }

  public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
    this.estimatedDeliveryTime = estimatedDeliveryTime;
  }
}
