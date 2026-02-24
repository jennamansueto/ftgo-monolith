package net.chrisrichardson.ftgo.restaurantservice.events;

import java.util.List;

public class MenuItemValidationResponse {

  private long restaurantId;
  private String restaurantName;
  private List<MenuItemDTO> menuItems;

  private MenuItemValidationResponse() {
  }

  public MenuItemValidationResponse(long restaurantId, String restaurantName, List<MenuItemDTO> menuItems) {
    this.restaurantId = restaurantId;
    this.restaurantName = restaurantName;
    this.menuItems = menuItems;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public List<MenuItemDTO> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItemDTO> menuItems) {
    this.menuItems = menuItems;
  }
}
