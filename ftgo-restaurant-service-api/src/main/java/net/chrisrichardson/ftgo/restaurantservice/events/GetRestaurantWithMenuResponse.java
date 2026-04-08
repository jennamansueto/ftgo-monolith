package net.chrisrichardson.ftgo.restaurantservice.events;

import net.chrisrichardson.ftgo.common.Money;

import java.util.List;

public class GetRestaurantWithMenuResponse {

    private long id;
    private String name;
    private List<MenuItemDTO> menuItems;
    private Money orderMinimum;

    private GetRestaurantWithMenuResponse() {
    }

    public GetRestaurantWithMenuResponse(long id, String name, List<MenuItemDTO> menuItems, Money orderMinimum) {
        this.id = id;
        this.name = name;
        this.menuItems = menuItems;
        this.orderMinimum = orderMinimum;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    public Money getOrderMinimum() {
        return orderMinimum;
    }
}
