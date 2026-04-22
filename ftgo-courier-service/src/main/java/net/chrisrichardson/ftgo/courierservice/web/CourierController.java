package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.api.AssignCourierActionsRequest;
import net.chrisrichardson.ftgo.courierservice.api.AvailableCourierDTO;
import net.chrisrichardson.ftgo.courierservice.api.CourierActionDTO;
import net.chrisrichardson.ftgo.courierservice.api.CourierAvailability;
import net.chrisrichardson.ftgo.courierservice.api.CourierNotFoundException;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierRequest;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierResponse;
import net.chrisrichardson.ftgo.courierservice.domain.Courier;
import net.chrisrichardson.ftgo.courierservice.domain.CourierAction;
import net.chrisrichardson.ftgo.courierservice.domain.CourierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class CourierController {

  private CourierService courierService;

  public CourierController(CourierService courierService) {
    this.courierService = courierService;
  }

  @RequestMapping(path = "/couriers", method = RequestMethod.POST)
  public ResponseEntity<CreateCourierResponse> create(@RequestBody CreateCourierRequest request) {
    Courier courier = courierService.createCourier(request.getName(), request.getAddress());
    return new ResponseEntity<>(new CreateCourierResponse(courier.getId()), HttpStatus.OK);
  }

  @RequestMapping(path = "/couriers/{courierId}/availability", method = RequestMethod.POST)
  public ResponseEntity<String> updateCourierAvailability(@PathVariable long courierId, @RequestBody CourierAvailability availability) {
    try {
      courierService.updateAvailability(courierId, availability.isAvailable());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path = "/couriers/available", method = RequestMethod.GET)
  public ResponseEntity<List<AvailableCourierDTO>> getAvailable() {
    List<AvailableCourierDTO> couriers = courierService.findAllAvailable().stream()
            .map(c -> new AvailableCourierDTO(c.getId()))
            .collect(toList());
    return new ResponseEntity<>(couriers, HttpStatus.OK);
  }

  @RequestMapping(path = "/couriers/{courierId}/actions", method = RequestMethod.POST)
  public ResponseEntity<List<CourierActionDTO>> assignActions(@PathVariable long courierId,
                                                              @RequestBody AssignCourierActionsRequest request) {
    try {
      Courier courier = courierService.assignOrder(courierId, request.getOrderId(), request.getReadyBy());
      List<CourierActionDTO> actions = courier.actionsForDelivery(request.getOrderId()).stream()
              .map(CourierController::toDto)
              .collect(toList());
      return new ResponseEntity<>(actions, HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path = "/couriers/{courierId}/orders/{orderId}/actions", method = RequestMethod.GET)
  public ResponseEntity<List<CourierActionDTO>> getActionsForOrder(@PathVariable long courierId,
                                                                   @PathVariable long orderId) {
    try {
      List<CourierActionDTO> actions = courierService.getActionsForOrder(courierId, orderId).stream()
              .map(CourierController::toDto)
              .collect(toList());
      return new ResponseEntity<>(actions, HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path = "/couriers/{courierId}", method = RequestMethod.GET)
  public ResponseEntity<Courier> get(@PathVariable long courierId) {
    try {
      return new ResponseEntity<>(courierService.findCourierById(courierId), HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  private static CourierActionDTO toDto(CourierAction action) {
    return new CourierActionDTO(action.getType(), action.getTime(), action.getOrderId());
  }

}
