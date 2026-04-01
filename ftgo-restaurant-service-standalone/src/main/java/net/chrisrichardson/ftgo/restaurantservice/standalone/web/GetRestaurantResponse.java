package net.chrisrichardson.ftgo.restaurantservice.standalone.web;

import net.chrisrichardson.ftgo.restaurantservice.standalone.Money;

import java.util.List;

public class GetRestaurantResponse {
  private Long id;
  private String name;
  private List<GetRestaurantResponse.MenuItem> menuItems;

  public GetRestaurantResponse() {
  }

  public GetRestaurantResponse(Long id, String name, List<GetRestaurantResponse.MenuItem> menuItems) {
    this.id = id;
    this.name = name;
    this.menuItems = menuItems;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<GetRestaurantResponse.MenuItem> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<GetRestaurantResponse.MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  public static class MenuItem {
    private String id;
    private String name;
    private Money price;

    public MenuItem() {
    }

    public MenuItem(String id, String name, Money price) {
      this.id = id;
      this.name = name;
      this.price = price;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Money getPrice() {
      return price;
    }

    public void setPrice(Money price) {
      this.price = price;
    }
  }
}
