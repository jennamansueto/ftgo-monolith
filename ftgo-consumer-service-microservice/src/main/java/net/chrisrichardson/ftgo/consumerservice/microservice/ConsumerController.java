package net.chrisrichardson.ftgo.consumerservice.microservice;

import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {

  private ConsumerMicroservice consumerMicroservice;

  public ConsumerController(ConsumerMicroservice consumerMicroservice) {
    this.consumerMicroservice = consumerMicroservice;
  }

  @RequestMapping(method = RequestMethod.POST)
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    return new CreateConsumerResponse(consumerMicroservice.create(request.getName()).getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    return consumerMicroservice.findById(consumerId)
            .map(consumer -> new ResponseEntity<>(new GetConsumerResponse(consumer.getId(), consumer.getName()), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<String> validateOrder(@PathVariable long consumerId, @RequestBody ValidateOrderRequest request) {
    try {
      consumerMicroservice.validateOrderForConsumer(consumerId, request.getOrderTotal());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ConsumerNotFoundException e) {
      return new ResponseEntity<>("Consumer not found", HttpStatus.NOT_FOUND);
    } catch (ConsumerVerificationFailedException e) {
      return new ResponseEntity<>("Consumer validation failed", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }
}
