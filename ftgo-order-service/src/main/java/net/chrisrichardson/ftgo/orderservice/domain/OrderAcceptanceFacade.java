package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.ActionRequest;
import net.chrisrichardson.ftgo.courierservice.api.GetCourierResponse;
import net.chrisrichardson.ftgo.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Facade that orchestrates the order acceptance flow.
 * Keeps HTTP calls outside of @Transactional boundaries to avoid
 * holding database connections idle during network I/O.
 *
 * Trade-off: if the DB write succeeds but the subsequent HTTP call to
 * add courier actions fails, the order will be marked as accepted with
 * a courier assigned, but the courier won't have the delivery actions.
 * This would need a compensating transaction or retry mechanism in production.
 */
public class OrderAcceptanceFacade {

  private static final Logger logger = LoggerFactory.getLogger(OrderAcceptanceFacade.class);

  private final OrderService orderService;
  private final CourierServiceClient courierServiceClient;
  private final Random random = new Random();

  public OrderAcceptanceFacade(OrderService orderService, CourierServiceClient courierServiceClient) {
    this.orderService = orderService;
    this.courierServiceClient = courierServiceClient;
  }

  /**
   * Accepts an order and schedules delivery with an available courier.
   * Steps:
   * 1. HTTP call to find available couriers (non-transactional)
   * 2. Transactional DB write to accept order and assign courier
   * 3. HTTP call to add pickup/dropoff actions to the courier (non-transactional)
   */
  public void accept(long orderId, LocalDateTime readyBy) {
    // Step 1: HTTP call - find an available courier (outside transaction)
    List<GetCourierResponse> couriers = courierServiceClient.findAllAvailable();
    GetCourierResponse courier = couriers.get(random.nextInt(couriers.size()));
    logger.info("Selected courier {} for order {}", courier.getId(), orderId);

    // Step 2: Transactional DB work - accept the order and assign courier
    Order order = orderService.acceptOrder(orderId, readyBy, courier.getId());

    // Step 3: HTTP call - schedule delivery actions on the courier (outside transaction)
    List<ActionRequest> actions = Arrays.asList(
            new ActionRequest("PICKUP", order.getId(), null),
            new ActionRequest("DROPOFF", order.getId(), readyBy.plusMinutes(30))
    );
    courierServiceClient.addActions(courier.getId(), actions);
    logger.info("Scheduled delivery for order {} with courier {}", orderId, courier.getId());
  }
}
