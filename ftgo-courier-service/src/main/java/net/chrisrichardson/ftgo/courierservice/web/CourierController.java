package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.api.AvailableCouriersResponse;
import net.chrisrichardson.ftgo.courierservice.api.CourierAvailability;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierRequest;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierResponse;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import net.chrisrichardson.ftgo.courierservice.domain.Courier;
import net.chrisrichardson.ftgo.courierservice.domain.CourierService;
import net.chrisrichardson.ftgo.courierservice.domain.NoAvailableCourierException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourierController {

  private CourierService courierService;

  public CourierController(CourierService courierService) {
    this.courierService = courierService;
  }

  @RequestMapping(path="/couriers", method= RequestMethod.POST)
  public ResponseEntity<CreateCourierResponse> create(@RequestBody CreateCourierRequest request) {
    Courier courier = courierService.createCourier(request.getName(), request.getAddress());
    return new ResponseEntity<>(new CreateCourierResponse(courier.getId()), HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/{courierId}/availability", method= RequestMethod.POST)
  public ResponseEntity<String> updateCourierLocation(@PathVariable long courierId, @RequestBody CourierAvailability availability) {
    courierService.updateAvailability(courierId, availability.isAvailable());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/available", method= RequestMethod.GET)
  public ResponseEntity<AvailableCouriersResponse> getAvailableCouriers() {
    return new ResponseEntity<>(new AvailableCouriersResponse(courierService.findAvailableCourierIds()), HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/scheduleDelivery", method= RequestMethod.POST)
  public ResponseEntity<ScheduleDeliveryResponse> scheduleDelivery(@RequestBody ScheduleDeliveryRequest request) {
    try {
      long courierId = courierService.scheduleDelivery(request.getOrderId(), request.getDropoffTime());
      return new ResponseEntity<>(new ScheduleDeliveryResponse(courierId), HttpStatus.OK);
    } catch (NoAvailableCourierException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path="/couriers/{courierId}", method= RequestMethod.GET)
  public ResponseEntity<Courier> get(@PathVariable long courierId) {
    Courier courier = courierService.findCourierById(courierId);
    return new ResponseEntity<>(courier, HttpStatus.OK);
  }

}
