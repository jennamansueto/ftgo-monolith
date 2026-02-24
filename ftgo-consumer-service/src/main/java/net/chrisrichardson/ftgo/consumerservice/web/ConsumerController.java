package net.chrisrichardson.ftgo.consumerservice.web;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
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

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<Void> validateOrderForConsumer(@PathVariable long consumerId,
                                                       @RequestBody ValidateOrderForConsumerRequest request) {
    consumerService.validateOrderForConsumer(consumerId, new Money(request.getOrderTotal()));
    return ResponseEntity.ok().build();
  }

  @ExceptionHandler(ConsumerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleConsumerNotFound(ConsumerNotFoundException ex) {
    return new ResponseEntity<>("Consumer not found", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConsumerVerificationFailedException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<String> handleConsumerVerificationFailed(ConsumerVerificationFailedException ex) {
    return new ResponseEntity<>("Consumer verification failed", HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
