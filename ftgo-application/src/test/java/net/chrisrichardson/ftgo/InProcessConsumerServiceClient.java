package net.chrisrichardson.ftgo;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.client.ConsumerServiceClient;
import org.springframework.web.client.RestTemplate;

/**
 * Test-only ConsumerServiceClient that performs no-op validation.
 * Used in FtgoApplicationTest where the consumer service is not running
 * as a separate process. Consumer creation is handled by a stub controller.
 */
public class InProcessConsumerServiceClient extends ConsumerServiceClient {

  public InProcessConsumerServiceClient() {
    super(new RestTemplate(), "http://unused");
  }

  @Override
  public void validateOrderForConsumer(long consumerId, Money orderTotal) {
    // No-op: in the test context, consumer validation always succeeds.
  }
}
