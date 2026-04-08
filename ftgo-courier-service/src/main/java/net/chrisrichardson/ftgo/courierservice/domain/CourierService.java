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

  void noteAvailable(long courierId) {
    courierRepository.findById(courierId).get().noteAvailable();
  }

  void noteUnavailable(long courierId) {
    courierRepository.findById(courierId).get().noteUnavailable();
  }

  public Courier findCourierById(long courierId) {
    return courierRepository.findById(courierId).get();
  }

  @Transactional
  public Courier assignDelivery(long orderId, LocalDateTime pickupTime, LocalDateTime dropoffTime) {
    List<Courier> couriers = courierRepository.findAllAvailable();
    if (couriers.isEmpty()) {
      throw new NoCourierAvailableException();
    }
    Courier courier = couriers.get(random.nextInt(couriers.size()));
    courier.addAction(Action.makePickupForOrder(orderId, pickupTime));
    courier.addAction(Action.makeDropoffForOrder(orderId, dropoffTime));
    return courier;
  }

}
