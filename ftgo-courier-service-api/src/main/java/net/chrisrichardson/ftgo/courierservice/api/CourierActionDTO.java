package net.chrisrichardson.ftgo.courierservice.api;

public class CourierActionDTO {
    private String type;
    private Long orderId;

    private CourierActionDTO() {}

    public CourierActionDTO(String type, Long orderId) {
        this.type = type;
        this.orderId = orderId;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
