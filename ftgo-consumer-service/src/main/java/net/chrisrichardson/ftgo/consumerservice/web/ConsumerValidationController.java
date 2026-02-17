package net.chrisrichardson.ftgo.consumerservice.web;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerResponse;
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
  public ResponseEntity<ValidateOrderForConsumerResponse> validateOrderForConsumer(
          @PathVariable long consumerId,
          @RequestBody ValidateOrderForConsumerRequest request) {
    try {
      consumerService.validateOrderForConsumer(consumerId, new Money(request.getOrderTotal()));
      return new ResponseEntity<>(new ValidateOrderForConsumerResponse(true, "Consumer validated successfully"), HttpStatus.OK);
    } catch (ConsumerVerificationFailedException e) {
      return new ResponseEntity<>(new ValidateOrderForConsumerResponse(false, "Consumer validation failed"), HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }
}
