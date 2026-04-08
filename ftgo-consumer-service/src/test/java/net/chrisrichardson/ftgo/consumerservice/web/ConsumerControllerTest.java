package net.chrisrichardson.ftgo.consumerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerVerificationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConsumerControllerTest {

  private ConsumerService consumerService;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() throws Exception {
    consumerService = mock(ConsumerService.class);
    ConsumerController controller = new ConsumerController();

    Field field = ConsumerController.class.getDeclaredField("consumerService");
    field.setAccessible(true);
    field.set(controller, consumerService);

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(converter)
            .build();
  }

  @Test
  public void shouldValidateConsumerForOrder() throws Exception {
    long consumerId = 1L;
    Money orderTotal = new Money("100.00");

    doNothing().when(consumerService).validateOrderForConsumer(eq(consumerId), any(Money.class));

    mockMvc.perform(post("/consumers/{consumerId}/validate", consumerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateOrderForConsumerRequest(orderTotal))))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404WhenConsumerNotFound() throws Exception {
    long consumerId = 999L;
    Money orderTotal = new Money("100.00");

    doThrow(new ConsumerNotFoundException())
            .when(consumerService).validateOrderForConsumer(eq(consumerId), any(Money.class));

    mockMvc.perform(post("/consumers/{consumerId}/validate", consumerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateOrderForConsumerRequest(orderTotal))))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturn422WhenVerificationFails() throws Exception {
    long consumerId = 1L;
    Money orderTotal = new Money("100.00");

    doThrow(new ConsumerVerificationFailedException())
            .when(consumerService).validateOrderForConsumer(eq(consumerId), any(Money.class));

    mockMvc.perform(post("/consumers/{consumerId}/validate", consumerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateOrderForConsumerRequest(orderTotal))))
            .andExpect(status().isUnprocessableEntity());
  }
}
