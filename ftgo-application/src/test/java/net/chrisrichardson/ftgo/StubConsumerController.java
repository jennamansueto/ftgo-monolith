package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test-only stub controller that handles consumer endpoints in the monolith's
 * test context. Since the consumer service has been extracted, these endpoints
 * no longer exist in the monolith — this stub provides them for in-process tests.
 */
@RestController
@RequestMapping(path = "/consumers")
public class StubConsumerController {

  private final AtomicLong idGenerator = new AtomicLong(1);
  private final Map<Long, CreateConsumerRequest> consumers = new ConcurrentHashMap<>();

  @RequestMapping(method = RequestMethod.POST)
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    long id = idGenerator.getAndIncrement();
    consumers.put(id, request);
    return new CreateConsumerResponse(id);
  }

  @RequestMapping(path = "/{consumerId}", method = RequestMethod.GET)
  public ResponseEntity<CreateConsumerResponse> get(@PathVariable long consumerId) {
    if (consumers.containsKey(consumerId)) {
      return new ResponseEntity<>(new CreateConsumerResponse(consumerId), HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
