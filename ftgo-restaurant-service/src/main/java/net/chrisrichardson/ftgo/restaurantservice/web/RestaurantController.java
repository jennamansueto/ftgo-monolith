package net.chrisrichardson.ftgo.restaurantservice.web;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.DuplicateMenuItemException;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItemNotFoundException;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantNotFoundException;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantService;
import net.chrisrichardson.ftgo.restaurantservice.events.AddMenuItemRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.GetMenuResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.UpdateMenuItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

  private GetRestaurantResponse makeGetRestaurantResponse(Restaurant r) {
    return new GetRestaurantResponse(r.getId(), r.getName());
  }

  @GetMapping("/{restaurantId}/menu")
  public ResponseEntity<GetMenuResponse> getMenu(@PathVariable long restaurantId) {
    try {
      List<MenuItem> menuItems = restaurantService.getMenu(restaurantId);
      List<MenuItemResponse> menuItemResponses = menuItems.stream()
              .map(this::toMenuItemResponse)
              .collect(Collectors.toList());
      return ResponseEntity.ok(new GetMenuResponse(menuItemResponses));
    } catch (RestaurantNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{restaurantId}/menu")
  public ResponseEntity<MenuItemResponse> addMenuItem(
          @PathVariable long restaurantId,
          @RequestBody AddMenuItemRequest request) {
    try {
      MenuItem item = restaurantService.addMenuItem(restaurantId, request);
      return ResponseEntity.status(HttpStatus.CREATED).body(toMenuItemResponse(item));
    } catch (RestaurantNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (DuplicateMenuItemException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{restaurantId}/menu/{itemId}")
  public ResponseEntity<MenuItemResponse> updateMenuItem(
          @PathVariable long restaurantId,
          @PathVariable String itemId,
          @RequestBody UpdateMenuItemRequest request) {
    try {
      MenuItem item = restaurantService.updateMenuItem(restaurantId, itemId, request);
      return ResponseEntity.ok(toMenuItemResponse(item));
    } catch (RestaurantNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (MenuItemNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{restaurantId}/menu/{itemId}")
  public ResponseEntity<Void> deleteMenuItem(
          @PathVariable long restaurantId,
          @PathVariable String itemId) {
    try {
      restaurantService.removeMenuItem(restaurantId, itemId);
      return ResponseEntity.noContent().build();
    } catch (RestaurantNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (MenuItemNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  private MenuItemResponse toMenuItemResponse(MenuItem item) {
    return new MenuItemResponse(item.getId(), item.getName(), item.getPrice());
  }
}
