package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantServiceClient restaurantServiceClient;

  private OrderTransactionHelper orderTransactionHelper;

  private CourierRepository courierRepository;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantServiceClient restaurantServiceClient,
                      OrderTransactionHelper orderTransactionHelper,
                      CourierRepository courierRepository) {

    this.orderRepository = orderRepository;
    this.restaurantServiceClient = restaurantServiceClient;
    this.orderTransactionHelper = orderTransactionHelper;
    this.courierRepository = courierRepository;
  }

  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    // HTTP call outside @Transactional boundary to avoid holding DB connection during network I/O
    Restaurant restaurant = restaurantServiceClient.findRestaurant(restaurantId);

    // Delegate to separate bean so Spring AOP can intercept @Transactional
    return orderTransactionHelper.createOrderInTransaction(consumerId, restaurant, lineItems);
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

  @Transactional
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
