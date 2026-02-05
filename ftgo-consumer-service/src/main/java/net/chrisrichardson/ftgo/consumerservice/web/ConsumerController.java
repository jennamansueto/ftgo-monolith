package net.chrisrichardson.ftgo.consumerservice.web;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerVerificationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/consumers")
public class ConsumerController {

  @Autowired
  private ConsumerService consumerService;

  @RequestMapping(method= RequestMethod.POST)
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    return new CreateConsumerResponse(consumerService.create(request.getName()).getId());
  }

  @RequestMapping(method= RequestMethod.GET,  path="/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    return consumerService.findById(consumerId)
            .map(consumer -> new ResponseEntity<>(new GetConsumerResponse(consumer.getName()), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method= RequestMethod.POST, path="/{consumerId}/validate")
  public ResponseEntity<ValidateOrderForConsumerResponse> validateOrderForConsumer(
          @PathVariable long consumerId,
          @RequestBody ValidateOrderForConsumerRequest request) {
    try {
      Money orderTotal = new Money(request.getOrderTotal());
      consumerService.validateOrderForConsumer(consumerId, orderTotal);
      return new ResponseEntity<>(new ValidateOrderForConsumerResponse(true), HttpStatus.OK);
    } catch (ConsumerNotFoundException e) {
      return new ResponseEntity<>(
              new ValidateOrderForConsumerResponse(false, "Consumer not found"),
              HttpStatus.NOT_FOUND);
    } catch (ConsumerVerificationFailedException e) {
      return new ResponseEntity<>(
              new ValidateOrderForConsumerResponse(false, "Consumer verification failed"),
              HttpStatus.BAD_REQUEST);
    }
  }
}
