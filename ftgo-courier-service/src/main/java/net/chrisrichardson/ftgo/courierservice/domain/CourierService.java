package net.chrisrichardson.ftgo.courierservice.domain;


import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.domain.Action;
import net.chrisrichardson.ftgo.domain.Courier;
import net.chrisrichardson.ftgo.domain.CourierRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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

  @Transactional
  public long scheduleDelivery(long orderId, LocalDateTime readyBy) {
    List<Courier> couriers = courierRepository.findAllAvailable();
    if (couriers.isEmpty()) {
      throw new RuntimeException("No available couriers");
    }
    Courier courier = couriers.get(random.nextInt(couriers.size()));
    courier.addAction(Action.makePickupForOrder(orderId));
    courier.addAction(Action.makeDropoffForOrder(orderId, readyBy.plusMinutes(30)));
    return courier.getId();
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

}
