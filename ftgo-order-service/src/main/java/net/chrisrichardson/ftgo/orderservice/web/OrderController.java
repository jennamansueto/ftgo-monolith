package net.chrisrichardson.ftgo.orderservice.web;

import net.chrisrichardson.ftgo.domain.MenuItem;
import net.chrisrichardson.ftgo.domain.Order;
import net.chrisrichardson.ftgo.domain.OrderLineItem;
import net.chrisrichardson.ftgo.domain.OrderRepository;
import net.chrisrichardson.ftgo.domain.OrderRevision;
import net.chrisrichardson.ftgo.domain.Restaurant;
import net.chrisrichardson.ftgo.orderservice.api.web.CreateOrderRequest;
import net.chrisrichardson.ftgo.orderservice.api.web.CreateOrderResponse;
import net.chrisrichardson.ftgo.orderservice.api.web.OrderAcceptance;
import net.chrisrichardson.ftgo.orderservice.api.web.ReviseOrderRequest;
import net.chrisrichardson.ftgo.orderservice.domain.InvalidMenuItemIdException;
import net.chrisrichardson.ftgo.orderservice.domain.OrderNotFoundException;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {

  private OrderService orderService;

  private OrderRepository orderRepository;

  private RestaurantServiceClient restaurantServiceClient;


  public OrderController(OrderService orderService, OrderRepository orderRepository,
                         RestaurantServiceClient restaurantServiceClient) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
    this.restaurantServiceClient = restaurantServiceClient;
  }

  @RequestMapping(method = RequestMethod.POST)
  public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
    Restaurant restaurant = restaurantServiceClient.findById(request.getRestaurantId());

    List<OrderLineItem> orderLineItems = makeOrderLineItems(
            request.getLineItems().stream()
                    .map(x -> new MenuItemIdAndQuantity(x.getMenuItemId(), x.getQuantity()))
                    .collect(toList()),
            restaurant);

    Order order = orderService.createOrder(request.getConsumerId(),
            restaurant.getId(),
            restaurant.getName(),
            orderLineItems
    );
    return new CreateOrderResponse(order.getId());
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
    return lineItems.stream().map(li -> {
      MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }


  @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
  public ResponseEntity<GetOrderResponse> getOrder(@PathVariable long orderId) {
    Optional<Order> order = orderRepository.findById(orderId);
    return order.map(o -> new ResponseEntity<>(makeGetOrderResponse(o), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<GetOrderResponse>> getOrders(@RequestParam(required = false) Long consumerId) {
    List<Order> orderList;
    if (consumerId != null) {
      orderList = orderRepository.findAllByConsumerId(consumerId);
    } else {
      orderList = (List<Order>) orderRepository.findAll();
    }
    List<GetOrderResponse> orders = orderList
            .stream()
            .map(this::makeGetOrderResponse)
            .collect(Collectors.toList());

    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  private GetOrderResponse makeGetOrderResponse(Order order) {
    return new GetOrderResponse(order.getId(),
            order.getOrderState().name(),
            order.getOrderTotal(),
            order.getRestaurantName(),
            order.getAssignedCourier() == null ? null : order.getAssignedCourier().getId(),
            order.getAssignedCourier() == null ? null : order.getAssignedCourier().actionsForDelivery(order)
    );
  }

  @RequestMapping(path = "/{orderId}/cancel", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponse> cancel(@PathVariable long orderId) {
    try {
      Order order = orderService.cancel(orderId);
      return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
    } catch (OrderNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path = "/{orderId}/revise", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponse> revise(@PathVariable long orderId, @RequestBody ReviseOrderRequest request) {
    try {
      Order order = orderService.reviseOrder(orderId, new OrderRevision(Optional.empty(), request.getRevisedLineItemQuantities()));
      return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
    } catch (OrderNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path="/{orderId}/accept", method= RequestMethod.POST)
  public ResponseEntity<String> accept(@PathVariable long orderId, @RequestBody OrderAcceptance orderAcceptance) {
    orderService.accept(orderId, orderAcceptance.getReadyBy());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path="/{orderId}/preparing", method= RequestMethod.POST)
  public ResponseEntity<String> preparing(@PathVariable long orderId) {
    orderService.notePreparing(orderId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path="/{orderId}/ready", method= RequestMethod.POST)
  public ResponseEntity<String> ready(@PathVariable long orderId) {
    orderService.noteReadyForPickup(orderId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path="/{orderId}/pickedup", method= RequestMethod.POST)
  public ResponseEntity<String> pickedup(@PathVariable long orderId) {
    orderService.notePickedUp(orderId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path="/{orderId}/delivered", method= RequestMethod.POST)
  public ResponseEntity<String> delivered(@PathVariable long orderId) {
    orderService.noteDelivered(orderId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
