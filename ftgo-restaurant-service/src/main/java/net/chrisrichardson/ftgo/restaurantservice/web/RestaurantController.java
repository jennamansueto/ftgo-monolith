package net.chrisrichardson.ftgo.restaurantservice.web;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantService;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemValidationResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.ValidateMenuItemsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/restaurants")
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantService;

  @RequestMapping(method = RequestMethod.POST)
  public CreateRestaurantResponse create(@RequestBody CreateRestaurantRequest request) {
    Restaurant r = restaurantService.create(request);
    return new CreateRestaurantResponse(r.getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{restaurantId}")
  public ResponseEntity<GetRestaurantResponse> get(@PathVariable long restaurantId) {
    return restaurantService.findById(restaurantId)
            .map(r -> new ResponseEntity<>(makeGetRestaurantResponse(r), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{restaurantId}/validateMenuItems")
  public ResponseEntity<MenuItemValidationResponse> validateMenuItems(
          @PathVariable long restaurantId,
          @RequestBody ValidateMenuItemsRequest request) {
    Optional<Restaurant> restaurantOpt = restaurantService.findById(restaurantId);
    if (!restaurantOpt.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Restaurant restaurant = restaurantOpt.get();
    List<MenuItemDTO> validatedItems = new ArrayList<>();
    for (String menuItemId : request.getMenuItemIds()) {
      Optional<MenuItem> menuItem = restaurant.findMenuItem(menuItemId);
      if (!menuItem.isPresent()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      MenuItem mi = menuItem.get();
      validatedItems.add(new MenuItemDTO(mi.getId(), mi.getName(), mi.getPrice()));
    }
    MenuItemValidationResponse response = new MenuItemValidationResponse(
            restaurant.getId(), restaurant.getName(), validatedItems);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private GetRestaurantResponse makeGetRestaurantResponse(Restaurant r) {
    return new GetRestaurantResponse(r.getId(), r.getName());
  }
}
