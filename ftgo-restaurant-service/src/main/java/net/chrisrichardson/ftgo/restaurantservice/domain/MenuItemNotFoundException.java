package net.chrisrichardson.ftgo.restaurantservice.domain;

public class MenuItemNotFoundException extends RuntimeException {

  public MenuItemNotFoundException(String itemId) {
    super("Menu item not found: " + itemId);
  }
}
