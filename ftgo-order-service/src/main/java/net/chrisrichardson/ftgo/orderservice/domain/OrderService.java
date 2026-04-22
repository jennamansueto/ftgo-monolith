package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.*;
import net.chrisrichardson.ftgo.orderservice.restaurantclient.RestaurantServiceClient;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.restaurantservice.events.GetRestaurantResponse;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItemDTO;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantMenuDTO;
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

  private RestaurantServiceClient restaurantServiceClient;

  private Optional<MeterRegistry> meterRegistry;

  private ConsumerService consumerService;
  private CourierRepository courierRepository;
  private Random random = new Random();

  public OrderService(OrderRepository orderRepository,
                      RestaurantServiceClient restaurantServiceClient,
                      Optional<MeterRegistry> meterRegistry,
                      ConsumerService consumerService, CourierRepository courierRepository) {

    this.orderRepository = orderRepository;
    this.restaurantServiceClient = restaurantServiceClient;
    this.meterRegistry = meterRegistry;
    this.consumerService = consumerService;
    this.courierRepository = courierRepository;
  }

  public Order createOrder(long consumerId, long restaurantId,
                           List<MenuItemIdAndQuantity> lineItems) {
    GetRestaurantResponse restaurant = restaurantServiceClient.findRestaurant(restaurantId);

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant.getMenu());

    return persistOrder(consumerId, restaurant, orderLineItems);
  }

  @Transactional
  public Order persistOrder(long consumerId,
                            GetRestaurantResponse restaurant,
                            List<OrderLineItem> orderLineItems) {
    Order order = new Order(consumerId, restaurant.getId(), restaurant.getName(), orderLineItems);

    consumerService.validateOrderForConsumer(consumerId, order.getOrderTotal());

    orderRepository.save(order);

    meterRegistry.ifPresent(mr1 -> mr1.counter("approved_orders").increment());

    meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

    return order;
  }

  private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, RestaurantMenuDTO menu) {
    return lineItems.stream().map(li -> {
      MenuItemDTO om = findMenuItem(menu, li.getMenuItemId())
              .orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
      return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
    }).collect(toList());
  }

  private Optional<MenuItemDTO> findMenuItem(RestaurantMenuDTO menu, String menuItemId) {
    if (menu == null || menu.getMenuItemDTOs() == null) {
      return Optional.empty();
    }
    return menu.getMenuItemDTOs().stream()
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
