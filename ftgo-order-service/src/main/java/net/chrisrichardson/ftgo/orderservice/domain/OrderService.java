package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantServiceClient restaurantServiceClient;

  private OrderPersistenceService orderPersistenceService;

  private CourierRepository courierRepository;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantServiceClient restaurantServiceClient,
                      OrderPersistenceService orderPersistenceService,
                      CourierRepository courierRepository) {

    this.orderRepository = orderRepository;
    this.restaurantServiceClient = restaurantServiceClient;
    this.orderPersistenceService = orderPersistenceService;
    this.courierRepository = courierRepository;
  }

  /**
   * Orchestrates order creation. The restaurant/menu lookup is a remote HTTP call and MUST NOT
   * run inside a database transaction (it would hold a JDBC connection idle during network I/O).
   * This method therefore runs with {@link Propagation#NOT_SUPPORTED} so any ambient transaction
   * is suspended; the database write is delegated to {@link OrderPersistenceService#saveOrder},
   * which runs in its own transaction.
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    RestaurantDTO restaurant = restaurantServiceClient.findRestaurant(restaurantId);

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

    return orderPersistenceService.saveOrder(consumerId, restaurantId, restaurant.getName(), orderLineItems);
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, RestaurantDTO restaurant) {
    return lineItems.stream().map(li -> {
      MenuItemDTO om = findMenuItem(restaurant, li.getMenuItemId())
              .orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }

  private Optional<MenuItemDTO> findMenuItem(RestaurantDTO restaurant, String menuItemId) {
    return restaurant.getMenuItems().stream()
            .filter(mi -> mi.getId().equals(menuItemId))
            .findFirst();
  }

  @Transactional
  public Order cancel(Long orderId) {
    Order order = tryToFindOrder(orderId);

    order.cancel();

    return order;
  }

  @Transactional
  public Order reviseOrder(long orderId, OrderRevision orderRevision) {
    Order order = tryToFindOrder(orderId);
    order.revise(orderRevision);
    return order;
  }

  public void accept(long orderId, LocalDateTime readyBy) {
    Order order = tryToFindOrder(orderId);
    order.acceptTicket(readyBy);
    scheduleDelivery(order, readyBy);
  }

  public void scheduleDelivery(Order order, LocalDateTime readyBy) {

    // Stupid implementation

    List<Courier> couriers = courierRepository.findAllAvailable();
    Courier courier = couriers.get(random.nextInt(couriers.size()));
    courier.addAction(Action.makePickup(order));
    courier.addAction(Action.makeDropoff(order, readyBy.plusMinutes(30)));

    order.schedule(courier);

  }


  private Order tryToFindOrder(Long orderId) {
    return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  @Transactional
  public void notePreparing(long orderId) {
    Order order = tryToFindOrder(orderId);
    order.notePreparing();
  }

  @Transactional
  public void noteReadyForPickup(long orderId) {
    Order order = tryToFindOrder(orderId);
    order.noteReadyForPickup();
  }

  @Transactional
  public void notePickedUp(long orderId) {
    Order order = tryToFindOrder(orderId);
    order.notePickedUp();
  }

  @Transactional
  public void noteDelivered(long orderId) {
    Order order = tryToFindOrder(orderId);
    order.noteDelivered();
  }
}
