package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantRepository restaurantRepository;

  private Optional<MeterRegistry> meterRegistry;

  private ConsumerServiceClient consumerServiceClient;
  private CourierRepository courierRepository;
  private TransactionTemplate transactionTemplate;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantRepository restaurantRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerServiceClient consumerServiceClient, CourierRepository courierRepository,
                      TransactionTemplate transactionTemplate) {

    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
    this.consumerServiceClient = consumerServiceClient;
    this.courierRepository = courierRepository;
    this.transactionTemplate = transactionTemplate;
  }

  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    // Read restaurant and compute order line items within a transaction so that
    // the lazy-loaded @ElementCollection menuItems can be accessed safely
    // without depending on OSIV (Open Session in View).
    List<OrderLineItem> orderLineItems = transactionTemplate.execute(status -> {
      Restaurant restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
      return makeOrderLineItems(lineItems, restaurant);
    });

    Money orderTotal = orderLineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add);

    // HTTP call is outside any transaction to avoid holding DB connections during network I/O
    consumerServiceClient.validateOrderForConsumer(consumerId, orderTotal);

    return transactionTemplate.execute(status -> {
      // Re-fetch the restaurant inside the write transaction for JPA association
      Restaurant restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
      Order order = new Order(consumerId, restaurant, orderLineItems);

      // TODO - charge a credit card too

      orderRepository.save(order);

      meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

      meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

      return order;
    });
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
    return lineItems.stream().map(li -> {
      MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }

  @Transactional
  public Order cancel(long orderId) {
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


  private Order tryToFindOrder(long orderId) {
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
