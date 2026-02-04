package net.chrisrichardson.ftgo.restaurantservice.events;

import java.util.List;

public class GetMenuResponse {

  private List<MenuItemResponse> menuItems;

  private GetMenuResponse() {
  }

  public GetMenuResponse(List<MenuItemResponse> menuItems) {
    this.menuItems = menuItems;
  }

  public List<MenuItemResponse> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItemResponse> menuItems) {
    this.menuItems = menuItems;
  }
}
