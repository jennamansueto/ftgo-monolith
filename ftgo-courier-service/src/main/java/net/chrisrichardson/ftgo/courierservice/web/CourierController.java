package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.api.CourierAvailability;
import net.chrisrichardson.ftgo.courierservice.api.CourierActionDto;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierRequest;
import net.chrisrichardson.ftgo.courierservice.api.CreateCourierResponse;
import net.chrisrichardson.ftgo.courierservice.api.GetCourierActionsForOrderResponse;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryRequest;
import net.chrisrichardson.ftgo.courierservice.api.ScheduleDeliveryResponse;
import net.chrisrichardson.ftgo.courierservice.domain.CourierService;
import net.chrisrichardson.ftgo.domain.Action;
import net.chrisrichardson.ftgo.domain.Courier;
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
  public ResponseEntity<Courier> get(@PathVariable long courierId) {
    Courier courier = courierService.findCourierById(courierId);
    return new ResponseEntity<>(courier, HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/schedule-delivery", method= RequestMethod.POST)
  public ResponseEntity<ScheduleDeliveryResponse> scheduleDelivery(@RequestBody ScheduleDeliveryRequest request) {
    long courierId = courierService.scheduleDelivery(request.getOrderId(), request.getReadyBy());
    return new ResponseEntity<>(new ScheduleDeliveryResponse(courierId), HttpStatus.OK);
  }

  @RequestMapping(path="/couriers/{courierId}/actions", method= RequestMethod.GET)
  public ResponseEntity<GetCourierActionsForOrderResponse> getCourierActionsForOrder(
          @PathVariable long courierId, @RequestParam long orderId) {
    List<Action> actions = courierService.getActionsForOrder(courierId, orderId);
    List<CourierActionDto> actionDtos = actions.stream()
            .map(a -> new CourierActionDto(a.getType().name(), orderId, null))
            .collect(Collectors.toList());
    return new ResponseEntity<>(new GetCourierActionsForOrderResponse(actionDtos), HttpStatus.OK);
  }

}
