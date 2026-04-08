package net.chrisrichardson.ftgo.courierservice.api;

public class AssignDeliveryResponse {
    private long courierId;

    private AssignDeliveryResponse() {
    }

    public AssignDeliveryResponse(long courierId) {
        this.courierId = courierId;
    }

    public long getCourierId() {
        return courierId;
    }

    public void setCourierId(long courierId) {
        this.courierId = courierId;
    }
}
