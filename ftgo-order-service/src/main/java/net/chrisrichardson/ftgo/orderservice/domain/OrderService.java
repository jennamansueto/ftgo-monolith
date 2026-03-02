package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.client.RestaurantServiceClient;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Transactional
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private Optional<MeterRegistry> meterRegistry;

  private ConsumerService consumerService;
  private CourierRepository courierRepository;
  private RestaurantServiceClient restaurantServiceClient;
  private OrderCreationService orderCreationService;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerService consumerService,
                      CourierRepository courierRepository,
                      RestaurantServiceClient restaurantServiceClient,
                      OrderCreationService orderCreationService) {

    this.orderRepository = orderRepository;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
    this.courierRepository = courierRepository;
    this.restaurantServiceClient = restaurantServiceClient;
    this.orderCreationService = orderCreationService;
  }

  /**
   * Creates an order. The restaurant menu is fetched via HTTP from the restaurant service
   * BEFORE entering the transactional boundary to avoid holding a DB connection during
   * network I/O.
   *
   * The transactional DB write is delegated to OrderCreationService (a separate Spring bean)
   * to avoid Spring's self-invocation proxy bypass issue, ensuring @Transactional is honored.
   *
   * Note: If the DB write fails after a successful restaurant service call, the restaurant
   * call cannot be rolled back (distributed transaction trade-off).
   */
  @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    // Fetch restaurant menu via HTTP - outside transaction
    RestaurantMenuDTO restaurantMenu = restaurantServiceClient.findRestaurantMenu(restaurantId);

    // Delegate to separate bean so @Transactional is honored (no self-invocation)
    return orderCreationService.createOrderTransactional(consumerId, restaurantId, lineItems, restaurantMenu);
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
