package net.chrisrichardson.ftgo.restaurantservice.events;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class RestaurantDTO {

  private long id;
  private String name;
  private List<MenuItemDTO> menuItems;

  private RestaurantDTO() {
  }

  public RestaurantDTO(long id, String name, List<MenuItemDTO> menuItems) {
    this.id = id;
    this.name = name;
    this.menuItems = menuItems;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<MenuItemDTO> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItemDTO> menuItems) {
    this.menuItems = menuItems;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
