package net.chrisrichardson.ftgo.restaurantservice.events;

public class GetRestaurantResponse {

  private Long id;
  private String name;
  private RestaurantMenuDTO menu;

  private GetRestaurantResponse() {
  }

  public GetRestaurantResponse(Long id, String name, RestaurantMenuDTO menu) {
    this.id = id;
    this.name = name;
    this.menu = menu;
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

  public RestaurantMenuDTO getMenu() {
    return menu;
  }

  public void setMenu(RestaurantMenuDTO menu) {
    this.menu = menu;
  }
}
