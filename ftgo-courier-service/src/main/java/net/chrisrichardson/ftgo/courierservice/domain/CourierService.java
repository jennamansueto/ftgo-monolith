package net.chrisrichardson.ftgo.courierservice.domain;


import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.courierservice.api.CourierNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class CourierService {

  private CourierRepository courierRepository;

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
  public void noteAvailable(long courierId) {
    findCourierById(courierId).noteAvailable();
  }

  @Transactional
  public void noteUnavailable(long courierId) {
    findCourierById(courierId).noteUnavailable();
  }

  @Transactional(readOnly = true)
  public Courier findCourierById(long courierId) {
    return courierRepository.findById(courierId).orElseThrow(() -> new CourierNotFoundException(courierId));
  }

  @Transactional(readOnly = true)
  public List<Courier> findAllAvailable() {
    return courierRepository.findAllAvailable();
  }

  @Transactional
  public Courier assignOrder(long courierId, long orderId, LocalDateTime readyBy) {
    Courier courier = findCourierById(courierId);
    courier.assignOrder(orderId, readyBy);
    return courier;
  }

  @Transactional(readOnly = true)
  public List<CourierAction> getActionsForOrder(long courierId, long orderId) {
    return findCourierById(courierId).actionsForDelivery(orderId);
  }

}
