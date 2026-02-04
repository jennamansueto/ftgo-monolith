package net.chrisrichardson.ftgo.orderservice.web;

import net.chrisrichardson.ftgo.domain.Action;

import java.time.LocalDateTime;
import java.util.List;

public class OrderTrackingResponse {
  private long orderId;
  private String state;
  private LocalDateTime estimatedPickupTime;
  private LocalDateTime estimatedDeliveryTime;
  private LocalDateTime readyBy;
  private LocalDateTime acceptTime;
  private LocalDateTime preparingTime;
  private LocalDateTime readyForPickupTime;
  private LocalDateTime pickedUpTime;
  private LocalDateTime deliveredTime;
  private Long assignedCourierId;
  private List<Action> courierActions;

  private OrderTrackingResponse() {
  }

  public OrderTrackingResponse(long orderId, String state, LocalDateTime estimatedPickupTime,
                               LocalDateTime estimatedDeliveryTime, LocalDateTime readyBy,
                               LocalDateTime acceptTime, LocalDateTime preparingTime,
                               LocalDateTime readyForPickupTime, LocalDateTime pickedUpTime,
                               LocalDateTime deliveredTime, Long assignedCourierId,
                               List<Action> courierActions) {
    this.orderId = orderId;
    this.state = state;
    this.estimatedPickupTime = estimatedPickupTime;
    this.estimatedDeliveryTime = estimatedDeliveryTime;
    this.readyBy = readyBy;
    this.acceptTime = acceptTime;
    this.preparingTime = preparingTime;
    this.readyForPickupTime = readyForPickupTime;
    this.pickedUpTime = pickedUpTime;
    this.deliveredTime = deliveredTime;
    this.assignedCourierId = assignedCourierId;
    this.courierActions = courierActions;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
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

  public LocalDateTime getReadyBy() {
    return readyBy;
  }

  public void setReadyBy(LocalDateTime readyBy) {
    this.readyBy = readyBy;
  }

  public LocalDateTime getAcceptTime() {
    return acceptTime;
  }

  public void setAcceptTime(LocalDateTime acceptTime) {
    this.acceptTime = acceptTime;
  }

  public LocalDateTime getPreparingTime() {
    return preparingTime;
  }

  public void setPreparingTime(LocalDateTime preparingTime) {
    this.preparingTime = preparingTime;
  }

  public LocalDateTime getReadyForPickupTime() {
    return readyForPickupTime;
  }

  public void setReadyForPickupTime(LocalDateTime readyForPickupTime) {
    this.readyForPickupTime = readyForPickupTime;
  }

  public LocalDateTime getPickedUpTime() {
    return pickedUpTime;
  }

  public void setPickedUpTime(LocalDateTime pickedUpTime) {
    this.pickedUpTime = pickedUpTime;
  }

  public LocalDateTime getDeliveredTime() {
    return deliveredTime;
  }

  public void setDeliveredTime(LocalDateTime deliveredTime) {
    this.deliveredTime = deliveredTime;
  }

  public Long getAssignedCourierId() {
    return assignedCourierId;
  }

  public void setAssignedCourierId(Long assignedCourierId) {
    this.assignedCourierId = assignedCourierId;
  }

  public List<Action> getCourierActions() {
    return courierActions;
  }

  public void setCourierActions(List<Action> courierActions) {
    this.courierActions = courierActions;
  }
}
