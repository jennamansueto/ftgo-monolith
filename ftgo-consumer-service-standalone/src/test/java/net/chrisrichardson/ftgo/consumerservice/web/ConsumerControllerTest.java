package net.chrisrichardson.ftgo.consumerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConsumerControllerTest {

  private ConsumerService consumerService;
  private ConsumerController consumerController;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    consumerService = mock(ConsumerService.class);
    consumerController = new ConsumerController();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());

    try {
      java.lang.reflect.Field field = ConsumerController.class.getDeclaredField("consumerService");
      field.setAccessible(true);
      field.set(consumerController, consumerService);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    mockMvc = MockMvcBuilders.standaloneSetup(consumerController).build();
  }

  @Test
  public void shouldCreateConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    Consumer consumer = new Consumer(name);

    try {
      java.lang.reflect.Field idField = Consumer.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(consumer, 1L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    when(consumerService.create(any(PersonName.class))).thenReturn(consumer);

    mockMvc.perform(post("/consumers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateConsumerRequest(name))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.consumerId").value(1));
  }

  @Test
  public void shouldGetConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    Consumer consumer = new Consumer(name);

    try {
      java.lang.reflect.Field idField = Consumer.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(consumer, 1L);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    when(consumerService.findById(1L)).thenReturn(Optional.of(consumer));

    mockMvc.perform(get("/consumers/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.consumerId").value(1))
            .andExpect(jsonPath("$.name.firstName").value("John"))
            .andExpect(jsonPath("$.name.lastName").value("Doe"));
  }

  @Test
  public void shouldReturn404WhenConsumerNotFound() throws Exception {
    when(consumerService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/999"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldValidateConsumer() throws Exception {
    doNothing().when(consumerService).validateOrderForConsumer(eq(1L), any(Money.class));

    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(new Money("100.00"));

    mockMvc.perform(post("/consumers/1/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404WhenValidatingNonExistentConsumer() throws Exception {
    doThrow(new ConsumerNotFoundException()).when(consumerService).validateOrderForConsumer(eq(999L), any(Money.class));

    ValidateOrderForConsumerRequest request = new ValidateOrderForConsumerRequest(new Money("100.00"));

    mockMvc.perform(post("/consumers/999/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
  }
}
