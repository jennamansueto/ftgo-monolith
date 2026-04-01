package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;

import java.util.List;
import java.util.Optional;

public class RestaurantDTO {

  private final long id;
  private final String name;
  private final List<MenuItemInfo> menuItems;

  public RestaurantDTO(long id, String name, List<MenuItemInfo> menuItems) {
    this.id = id;
    this.name = name;
    this.menuItems = menuItems;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<MenuItemInfo> getMenuItems() {
    return menuItems;
  }

  public Optional<MenuItemInfo> findMenuItem(String menuItemId) {
    return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
  }

  public static class MenuItemInfo {
    private final String id;
    private final String name;
    private final Money price;

    public MenuItemInfo(String id, String name, Money price) {
      this.id = id;
      this.name = name;
      this.price = price;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public Money getPrice() {
      return price;
    }
  }
}
