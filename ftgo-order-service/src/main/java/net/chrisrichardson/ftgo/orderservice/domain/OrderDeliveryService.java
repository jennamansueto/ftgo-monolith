package net.chrisrichardson.ftgo.orderservice.domain;

import java.time.LocalDateTime;

/**
 * Orchestrates accepting an order and scheduling its delivery.
 *
 * The remote HTTP call to the courier service is performed OUTSIDE of any database transaction
 * (this class is not @Transactional). Only after the courier has been chosen do we invoke the
 * transactional {@link OrderService#acceptOrderAndAssignCourier} method, which holds a DB
 * connection only for the duration of the local database work.
 *
 * Trade-off: because the remote call and the DB write are no longer in a single transaction, a
 * failure of the DB write after a successful courier assignment cannot be rolled back atomically.
 */
public class OrderDeliveryService {

  private final CourierServiceClient courierServiceClient;
  private final OrderService orderService;

  public OrderDeliveryService(CourierServiceClient courierServiceClient, OrderService orderService) {
    this.courierServiceClient = courierServiceClient;
    this.orderService = orderService;
  }

  public void accept(long orderId, LocalDateTime readyBy) {
    // 1) Remote call to the courier service - OUTSIDE the transaction boundary.
    long courierId = courierServiceClient.scheduleDelivery(orderId, readyBy.plusMinutes(30));

    // 2) Local, transactional database work.
    orderService.acceptOrderAndAssignCourier(orderId, readyBy, courierId);
  }
}
