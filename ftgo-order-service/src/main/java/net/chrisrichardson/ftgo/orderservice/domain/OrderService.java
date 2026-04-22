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
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantRepository restaurantRepository,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerService consumerService,
                      CourierServiceClient courierServiceClient) {

    this.orderRepository = orderRepository;
    this.restaurantRepository = restaurantRepository;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
    this.courierServiceClient = courierServiceClient;
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
   * Note: This method is intentionally not @Transactional. HTTP calls to the
   * courier service happen outside any database transaction. The acceptance
   * state transition and the assigned courier id are persisted in a single
   * transactional method ({@link #persistAcceptance}) after the HTTP calls
   * complete successfully.
   *
   * Atomicity trade-off: if the HTTP call to assign the courier succeeds but
   * the subsequent local DB commit fails (or vice-versa), the two sides can
   * drift. A future enhancement is to use sagas / outbox for at-least-once
   * delivery and compensation.
   */
  public void accept(long orderId, LocalDateTime readyBy) {
    // Verify order exists before calling remote service.
    loadOrderReadOnly(orderId);

    long courierId = assignAvailableCourier(orderId, readyBy);

    persistAcceptance(orderId, readyBy, courierId);
  }

  @Transactional(readOnly = true)
  protected Order loadOrderReadOnly(long orderId) {
    return tryToFindOrder(orderId);
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

  @Transactional
  protected void persistAcceptance(long orderId, LocalDateTime readyBy, long courierId) {
    Order order = tryToFindOrder(orderId);
    order.acceptTicket(readyBy);
    order.schedule(courierId);
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
