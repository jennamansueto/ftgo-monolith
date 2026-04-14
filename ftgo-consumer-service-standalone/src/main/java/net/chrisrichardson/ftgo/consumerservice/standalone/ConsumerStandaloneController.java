package net.chrisrichardson.ftgo.consumerservice.standalone;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerVerificationFailedException;
import net.chrisrichardson.ftgo.domain.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/consumers")
public class ConsumerStandaloneController {

  @Autowired
  private ConsumerService consumerService;

  @RequestMapping(method = RequestMethod.POST)
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    return new CreateConsumerResponse(consumerService.create(request.getName()).getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/{consumerId}")
  public ResponseEntity<GetConsumerStandaloneResponse> get(@PathVariable long consumerId) {
    return consumerService.findById(consumerId)
            .map(consumer -> new ResponseEntity<>(new GetConsumerStandaloneResponse(consumer.getId(), consumer.getName()), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{consumerId}/validate")
  public ResponseEntity<Void> validate(@PathVariable long consumerId, @RequestBody ValidateOrderRequest request) {
    try {
      consumerService.validateOrderForConsumer(consumerId, request.getOrderTotal());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ConsumerVerificationFailedException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public static class GetConsumerStandaloneResponse {
    private long consumerId;
    private PersonName name;

    private GetConsumerStandaloneResponse() {
    }

    public GetConsumerStandaloneResponse(long consumerId, PersonName name) {
      this.consumerId = consumerId;
      this.name = name;
    }

    public long getConsumerId() {
      return consumerId;
    }

    public PersonName getName() {
      return name;
    }
  }

  public static class ValidateOrderRequest {
    private Money orderTotal;

    private ValidateOrderRequest() {
    }

    public ValidateOrderRequest(Money orderTotal) {
      this.orderTotal = orderTotal;
    }

    public Money getOrderTotal() {
      return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
      this.orderTotal = orderTotal;
    }
  }
}
