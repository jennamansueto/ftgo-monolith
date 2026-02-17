package net.chrisrichardson.ftgo.restaurantservice.persistence;

import net.chrisrichardson.ftgo.common.Address;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "restaurants")
@Access(AccessType.FIELD)
@DynamicUpdate
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Embedded
  private Address address;

  @Embedded
  @ElementCollection
  @CollectionTable(name = "restaurant_menu_items")
  private List<MenuItemEntity> menuItems;

  public RestaurantEntity() {
  }

  public RestaurantEntity(String name, Address address, List<MenuItemEntity> menuItems) {
    this.name = name;
    this.address = address;
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

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<MenuItemEntity> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItemEntity> menuItems) {
    this.menuItems = menuItems;
  }

  public Optional<MenuItemEntity> findMenuItem(String menuItemId) {
    return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
  }
}
