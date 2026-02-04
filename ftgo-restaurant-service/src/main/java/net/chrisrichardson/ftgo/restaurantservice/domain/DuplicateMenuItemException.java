package net.chrisrichardson.ftgo.restaurantservice.domain;

public class DuplicateMenuItemException extends RuntimeException {

  public DuplicateMenuItemException(String itemId) {
    super("Menu item already exists with ID: " + itemId);
  }
}
