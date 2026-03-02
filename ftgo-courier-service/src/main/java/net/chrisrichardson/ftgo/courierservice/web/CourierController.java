package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.api.CourierAvailability;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierRequest;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierResponse;
import net.chrisrichardson.ftgo.courierservice.domain.CourierNotFoundException;
import net.chrisrichardson.ftgo.courierservice.domain.CourierService;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierAction;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierActionType;
import net.chrisrichardson.ftgo.courierservice.persistence.CourierEntity;
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
    CourierEntity courier = courierService.createCourier(request.getName(), request.getAddress());
    return new ResponseEntity<>(new CreateCourierResponse(courier.getId()), HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/{courierId}/availability", method= RequestMethod.POST)
  public ResponseEntity<String> updateCourierLocation(@PathVariable long courierId, @RequestBody CourierAvailability availability) {
    try {
      courierService.updateAvailability(courierId, availability.isAvailable());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path="/couriers/{courierId}", method= RequestMethod.GET)
  public ResponseEntity<GetCourierResponse> get(@PathVariable long courierId) {
    try {
      CourierEntity courier = courierService.findCourierById(courierId);
      return new ResponseEntity<>(makeGetCourierResponse(courier), HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path="/couriers/available", method= RequestMethod.GET)
  public ResponseEntity<List<GetCourierResponse>> getAvailable() {
    List<CourierEntity> couriers = courierService.findAllAvailable();
    List<GetCourierResponse> response = couriers.stream()
            .map(this::makeGetCourierResponse)
            .collect(Collectors.toList());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/{courierId}/actions", method= RequestMethod.POST)
  public ResponseEntity<String> addAction(@PathVariable long courierId, @RequestBody AddActionRequest request) {
    try {
      CourierAction action = new CourierAction(
              CourierActionType.valueOf(request.getType()),
              request.getOrderId(),
              request.getTime()
      );
      courierService.addAction(courierId, action);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (CourierNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  private GetCourierResponse makeGetCourierResponse(CourierEntity courier) {
    return new GetCourierResponse(
            courier.getId(),
            courier.isAvailable(),
            courier.getActions()
    );
  }

}
