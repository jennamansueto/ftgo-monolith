package net.chrisrichardson.ftgo.domain;

import net.chrisrichardson.ftgo.common.Address;

import java.util.List;
import java.util.Optional;

/**
 * Restaurant POJO retained for backward compatibility (used by test helpers
 * and the Order constructor). No longer a JPA entity — the restaurant service
 * owns its own RestaurantEntity mapped to the "restaurants" table.
 */
public class Restaurant {

  private Long id;

  private String name;

  private Address address;

  private List<MenuItem> menuItems;

  public Restaurant() {
  }

  public Restaurant(String name, Address address, RestaurantMenu menu) {
    this.name = name;
    this.address = address;
    this.menuItems = menu.getMenuItems();
  }

  public Restaurant(Long id, String name, RestaurantMenu menu) {
    this.id = id;
    this.name = name;
    this.menuItems = menu.getMenuItems();
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

  public Long getId() {
    return id;
  }

  public Address getAddress() {
    return address;
  }

  public Optional<MenuItem> findMenuItem(String menuItemId) {
    return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
  }

  public void reviseMenu(RestaurantMenu revisedMenu) {
    throw new UnsupportedOperationException();
  }
}
