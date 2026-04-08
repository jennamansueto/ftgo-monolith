package net.chrisrichardson.ftgo.courierservice.api;

import java.time.LocalDateTime;

public class AssignDeliveryRequest {
    private long orderId;
    private LocalDateTime pickupTime;
    private LocalDateTime dropoffTime;

    private AssignDeliveryRequest() {
    }

    public AssignDeliveryRequest(long orderId, LocalDateTime pickupTime, LocalDateTime dropoffTime) {
        this.orderId = orderId;
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
    }

    public long getOrderId() {
        return orderId;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public LocalDateTime getDropoffTime() {
        return dropoffTime;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public void setDropoffTime(LocalDateTime dropoffTime) {
        this.dropoffTime = dropoffTime;
    }
}
