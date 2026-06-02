package net.chrisrichardson.ftgo.courierservice.domain;


import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.PersonName;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class CourierService {

  private CourierRepository courierRepository;
  private Random random = new Random();

  public CourierService(CourierRepository courierRepository) {
    this.courierRepository = courierRepository;
  }

  @Transactional
  public void updateAvailability(long courierId, boolean available) {
    if (available)
      noteAvailable(courierId);
    else
      noteUnavailable(courierId);
  }

  @Transactional
  public Courier createCourier(PersonName name, Address address) {
    Courier courier = new Courier(name, address);
    courierRepository.save(courier);
    return courier;
  }

  void noteAvailable(long courierId) {
    courierRepository.findById(courierId).get().noteAvailable();
  }

  void noteUnavailable(long courierId) {
    courierRepository.findById(courierId).get().noteUnavailable();
  }

  public Courier findCourierById(long courierId) {
    return courierRepository.findById(courierId).get();
  }

  @Transactional(readOnly = true)
  public List<Long> findAvailableCourierIds() {
    return courierRepository.findAllAvailable().stream().map(Courier::getId).collect(toList());
  }

  /**
   * Selects an available courier and assigns the pickup/dropoff actions for the given order.
   * This is the logic that previously lived in OrderService.scheduleDelivery in the monolith.
   *
   * @return the id of the courier the delivery was scheduled with
   */
  @Transactional
  public long scheduleDelivery(long orderId, LocalDateTime dropoffTime) {
    List<Courier> couriers = courierRepository.findAllAvailable();
    if (couriers.isEmpty())
      throw new NoAvailableCourierException();
    Courier courier = couriers.get(random.nextInt(couriers.size()));
    courier.addAction(Action.makePickup(orderId));
    courier.addAction(Action.makeDropoff(orderId, dropoffTime));
    return courier.getId();
  }

}
