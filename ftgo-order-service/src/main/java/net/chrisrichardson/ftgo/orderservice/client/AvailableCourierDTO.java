package net.chrisrichardson.ftgo.orderservice.client;

import java.time.LocalDateTime;
import java.util.List;

public class AvailableCourierDTO {

  private long id;
  private boolean available;
  private List<CourierActionDTO> actions;

  private AvailableCourierDTO() {
  }

  public AvailableCourierDTO(long id, boolean available, List<CourierActionDTO> actions) {
    this.id = id;
    this.available = available;
    this.actions = actions;
  }

  public long getId() {
    return id;
  }

  public boolean isAvailable() {
    return available;
  }

  public List<CourierActionDTO> getActions() {
    return actions;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void setActions(List<CourierActionDTO> actions) {
    this.actions = actions;
  }

  public static class CourierActionDTO {
    private String type;
    private Long orderId;
    private LocalDateTime time;

    private CourierActionDTO() {
    }

    public CourierActionDTO(String type, Long orderId, LocalDateTime time) {
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
}
