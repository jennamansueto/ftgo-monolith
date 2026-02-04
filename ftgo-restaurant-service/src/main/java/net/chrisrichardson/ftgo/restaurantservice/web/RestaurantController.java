package net.chrisrichardson.ftgo.restaurantservice.web;

import net.chrisrichardson.ftgo.domain.OrderMessage;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.OrderNotBelongToRestaurantException;
import net.chrisrichardson.ftgo.restaurantservice.domain.OrderNotFoundException;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantNotFoundException;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantService;
import net.chrisrichardson.ftgo.restaurantservice.events.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.SendMessageRequest;
import net.chrisrichardson.ftgo.restaurantservice.events.SendMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @RequestMapping(path = "/{restaurantId}/orders/{orderId}/messages", method = RequestMethod.POST)
  public ResponseEntity<SendMessageResponse> sendMessage(
          @PathVariable long restaurantId,
          @PathVariable long orderId,
          @RequestBody SendMessageRequest request) {
    try {
      OrderMessage orderMessage = restaurantService.sendMessage(restaurantId, orderId, request.getMessage());
      SendMessageResponse response = new SendMessageResponse(
              orderMessage.getId(),
              orderMessage.getOrder().getId(),
              orderMessage.getMessage(),
              orderMessage.getCreatedAt()
      );
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (RestaurantNotFoundException | OrderNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (OrderNotBelongToRestaurantException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
