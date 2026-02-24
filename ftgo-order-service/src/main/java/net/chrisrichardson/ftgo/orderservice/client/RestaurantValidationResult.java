package net.chrisrichardson.ftgo.orderservice.client;

import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;

import java.util.Map;

public class RestaurantValidationResult {

  private final long restaurantId;
  private final String restaurantName;
  private final Map<String, MenuItemDTO> menuItems;

  public RestaurantValidationResult(long restaurantId, String restaurantName, Map<String, MenuItemDTO> menuItems) {
    this.restaurantId = restaurantId;
    this.restaurantName = restaurantName;
    this.menuItems = menuItems;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public Map<String, MenuItemDTO> getMenuItems() {
    return menuItems;
  }
}
