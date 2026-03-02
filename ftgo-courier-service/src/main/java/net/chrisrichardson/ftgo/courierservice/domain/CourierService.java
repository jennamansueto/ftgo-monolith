package net.chrisrichardson.ftgo.courierservice.domain;


import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierAction;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierEntity;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierEntityRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class CourierService {

  private CourierEntityRepository courierEntityRepository;

  public CourierService(CourierEntityRepository courierEntityRepository) {
    this.courierEntityRepository = courierEntityRepository;
  }

  public void updateAvailability(long courierId, boolean available) {
    if (available)
      noteAvailable(courierId);
    else
      noteUnavailable(courierId);
  }

  public CourierEntity createCourier(PersonName name, Address address) {
    CourierEntity courier = new CourierEntity(name, address);
    courierEntityRepository.save(courier);
    return courier;
  }

  void noteAvailable(long courierId) {
    findCourierById(courierId).noteAvailable();
  }

  void noteUnavailable(long courierId) {
    findCourierById(courierId).noteUnavailable();
  }

  public CourierEntity findCourierById(long courierId) {
    return courierEntityRepository.findById(courierId)
            .orElseThrow(() -> new CourierNotFoundException(courierId));
  }

  public List<CourierEntity> findAllAvailable() {
    return courierEntityRepository.findAllAvailable();
  }

  public void addAction(long courierId, CourierAction action) {
    CourierEntity courier = findCourierById(courierId);
    courier.addAction(action);
  }

}
