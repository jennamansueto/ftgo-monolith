package net.chrisrichardson.ftgo.restaurantservice.events;

import java.util.List;

public class ValidateMenuItemsRequest {

  private List<String> menuItemIds;

  private ValidateMenuItemsRequest() {
  }

  public ValidateMenuItemsRequest(List<String> menuItemIds) {
    this.menuItemIds = menuItemIds;
  }

  public List<String> getMenuItemIds() {
    return menuItemIds;
  }

  public void setMenuItemIds(List<String> menuItemIds) {
    this.menuItemIds = menuItemIds;
  }
}
