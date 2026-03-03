package net.chrisrichardson.ftgo.consumerservice.standalone;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerVerificationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerValidationController {

  @Autowired
  private ConsumerService consumerService;

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<Void> validateOrder(@PathVariable long consumerId,
                                            @RequestBody ValidateOrderRequest request) {
    try {
      consumerService.validateOrderForConsumer(consumerId, new Money(request.getOrderTotal()));
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ConsumerNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (ConsumerVerificationFailedException e) {
      return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }
}
