package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.consumerclient.ConsumerServiceClient;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private OrderRepository orderRepository;

  private RestaurantRepository restaurantRepository;

  private Optional<MeterRegistry> meterRegistry;

  private ConsumerServiceClient consumerServiceClient;
  private CourierRepository courierRepository;
  private TransactionTemplate readOnlyTransactionTemplate;
  private TransactionTemplate writeTransactionTemplate;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantRepository restaurantRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerServiceClient consumerServiceClient,
                      CourierRepository courierRepository,
                      PlatformTransactionManager transactionManager) {

    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
    this.consumerServiceClient = consumerServiceClient;
    this.courierRepository = courierRepository;
    this.readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
    this.readOnlyTransactionTemplate.setReadOnly(true);
    this.writeTransactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Transactional(propagation = Propagation.NEVER)
  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    // Step 1: compute the order's line items and total in a read-only transaction.
    // OrderLineItem carries denormalized name/price, so the items returned here are
    // self-contained and can be persisted later without re-reading the restaurant
    // menu (which would introduce a TOCTOU race against the HTTP validation).
    List<OrderLineItem> orderLineItems = readOnlyTransactionTemplate.execute(status -> {
      Restaurant restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
      return makeOrderLineItems(lineItems, restaurant);
    });
    Money orderTotal = orderLineItems.stream()
            .map(OrderLineItem::getTotal)
            .reduce(Money.ZERO, Money::add);

    // Step 2: HTTP call to Consumer Service OUTSIDE any DB transaction. If this
    // throws, no order is persisted.
    consumerServiceClient.validateOrderForConsumer(consumerId, orderTotal);

    // Step 3: persist the order in a separate write transaction, reusing the line
    // items computed in step 1 so the persisted total matches the validated total.
    return writeTransactionTemplate.execute(status -> {
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
