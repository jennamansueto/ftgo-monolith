package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.courierservice.api.AvailableCourierDTO;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.courier.CourierServiceClient;
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
  private TransactionTemplate readOnlyTransactionTemplate;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantRepository restaurantRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerService consumerService,
                      CourierServiceClient courierServiceClient,
                      TransactionTemplate transactionTemplate,
                      TransactionTemplate readOnlyTransactionTemplate) {

    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
    this.courierServiceClient = courierServiceClient;
    this.transactionTemplate = transactionTemplate;
    this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
  }

  @Transactional
  public Order createOrder(long consumerId,
                           long restaurantId,
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
   * Accepts an order and schedules a courier.
   *
   * HTTP calls to the courier service happen outside any database transaction.
   * The order-existence check and the local state transition are each wrapped
   * in their own transaction via {@link TransactionTemplate} so that remote
   * calls never run inside a @Transactional boundary. TransactionTemplate is
   * used instead of @Transactional on private methods because Spring's proxy
   * AOP would not intercept self-invocation from within the same bean.
   *
   * Atomicity trade-off: if the HTTP call to assign the courier succeeds but
   * the subsequent local DB commit fails (or vice-versa), the two sides can
   * drift. A future enhancement is to use sagas / outbox for at-least-once
   * delivery and compensation.
   */
  public void accept(long orderId, LocalDateTime readyBy) {
    readOnlyTransactionTemplate.execute(status -> tryToFindOrder(orderId));

    long courierId = assignAvailableCourier(orderId, readyBy);

    transactionTemplate.execute(status -> {
      Order order = tryToFindOrder(orderId);
      order.acceptTicket(readyBy);
      order.schedule(courierId);
      return null;
    });
  }

  private long assignAvailableCourier(long orderId, LocalDateTime readyBy) {
    List<AvailableCourierDTO> couriers = courierServiceClient.findAvailableCouriers();
    if (couriers.isEmpty()) {
      throw new NoAvailableCouriersException();
    }
    AvailableCourierDTO picked = couriers.get(random.nextInt(couriers.size()));
    courierServiceClient.assignOrderToCourier(picked.getId(), orderId, readyBy);
    return picked.getId();
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
