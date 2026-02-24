package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.api.CourierAvailability;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierRequest;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierResponse;
import net.chrisrichardson.ftgo.courierservice.domain.Action;
import net.chrisrichardson.ftgo.courierservice.domain.Courier;
import net.chrisrichardson.ftgo.courierservice.domain.CourierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

  @RequestMapping(path="/couriers/{courierId}", method= RequestMethod.GET)
  public ResponseEntity<GetCourierResponse> get(@PathVariable long courierId) {
    Courier courier = courierService.findCourierById(courierId);
    return new ResponseEntity<>(toResponse(courier), HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/available", method= RequestMethod.GET)
  public ResponseEntity<List<GetCourierResponse>> getAvailable() {
    List<Courier> couriers = courierService.findAllAvailable();
    List<GetCourierResponse> response = couriers.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/{courierId}/actions", method= RequestMethod.POST)
  public ResponseEntity<String> addActions(@PathVariable long courierId, @RequestBody List<ActionRequest> actionRequests) {
    List<Action> actions = actionRequests.stream()
            .map(ar -> new Action(ar.getType(), ar.getOrderId(), ar.getTime()))
            .collect(Collectors.toList());
    courierService.addActions(courierId, actions);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private GetCourierResponse toResponse(Courier courier) {
    return new GetCourierResponse(courier.getId(), courier.isAvailable());
  }

}
