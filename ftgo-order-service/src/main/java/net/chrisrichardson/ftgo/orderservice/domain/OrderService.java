package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.client.AvailableCourierDTO;
import net.chrisrichardson.ftgo.orderservice.client.CourierServiceClient;
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

  private ConsumerService consumerService;
  private CourierServiceClient courierServiceClient;
  private TransactionTemplate transactionTemplate;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantRepository restaurantRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerService consumerService, CourierServiceClient courierServiceClient,
                      TransactionTemplate transactionTemplate) {

    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
    this.courierServiceClient = courierServiceClient;
    this.transactionTemplate = transactionTemplate;
  }

  @Transactional
  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));


    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

    Order order = new Order(consumerId, restaurant, orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    // TODO - charge a credit card too

    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
    return lineItems.stream().map(li -> {
      MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
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

  /**
   * Accepts an order and schedules delivery.
   * The HTTP call to the courier service is made BEFORE the transactional DB work
   * to avoid holding a DB connection idle during network I/O.
   *
   * Note: if the DB write fails after the courier service has been updated,
   * the courier actions cannot be rolled back atomically (eventual consistency trade-off).
   */
  public void accept(long orderId, LocalDateTime readyBy) {
    // Step 1: Find an available courier via HTTP (non-transactional)
    List<AvailableCourierDTO> couriers = courierServiceClient.findAllAvailable();
    AvailableCourierDTO selectedCourier = couriers.get(random.nextInt(couriers.size()));

    // Step 2: Add PICKUP and DROPOFF actions via HTTP (non-transactional)
    courierServiceClient.addAction(selectedCourier.getId(), "PICKUP", orderId, null);
    courierServiceClient.addAction(selectedCourier.getId(), "DROPOFF", orderId, readyBy.plusMinutes(30));

    // Step 3: Update the order in a transactional context using TransactionTemplate
    // (self-invocation of @Transactional methods doesn't work with Spring proxies)
    transactionTemplate.execute(status -> {
      Order order = tryToFindOrder(orderId);
      order.acceptTicket(readyBy);
      order.scheduleWithCourierId(selectedCourier.getId());
      return null;
    });
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
