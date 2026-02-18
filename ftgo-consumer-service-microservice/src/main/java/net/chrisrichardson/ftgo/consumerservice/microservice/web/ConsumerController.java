package net.chrisrichardson.ftgo.consumerservice.microservice.web;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.microservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.microservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.microservice.domain.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {

  @Autowired
  private ConsumerService consumerService;

  @RequestMapping(method = RequestMethod.POST)
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    Consumer consumer = consumerService.create(request.getName());
    return new CreateConsumerResponse(consumer.getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    return consumerService.findById(consumerId)
            .map(consumer -> new ResponseEntity<>(new GetConsumerResponse(consumer.getId(), consumer.getName()), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<Void> validateOrder(@PathVariable long consumerId,
                                            @RequestBody ValidateOrderRequest request) {
    try {
      consumerService.validateOrderForConsumer(consumerId, request.getOrderTotal());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ConsumerNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
