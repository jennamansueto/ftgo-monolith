package net.chrisrichardson.ftgo.courierservice.api;

import java.util.ArrayList;
import java.util.List;

public class AvailableCouriersResponse {

  private List<Long> courierIds = new ArrayList<>();

  public AvailableCouriersResponse() {
  }

  public AvailableCouriersResponse(List<Long> courierIds) {
    this.courierIds = courierIds;
  }

  public List<Long> getCourierIds() {
    return courierIds;
  }

  public void setCourierIds(List<Long> courierIds) {
    this.courierIds = courierIds;
  }
}
