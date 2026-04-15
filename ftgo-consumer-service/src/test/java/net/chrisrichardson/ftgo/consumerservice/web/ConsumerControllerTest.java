package net.chrisrichardson.ftgo.consumerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConsumerControllerTest {

  private ConsumerService consumerService;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    consumerService = mock(ConsumerService.class);
    ConsumerController controller = new ConsumerController();

    // Use reflection to set the @Autowired field
    try {
      java.lang.reflect.Field field = ConsumerController.class.getDeclaredField("consumerService");
      field.setAccessible(true);
      field.set(controller, consumerService);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(converter)
            .build();
  }

  @Test
  public void shouldCreateConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    Consumer consumer = new Consumer(name);

    when(consumerService.create(any(PersonName.class))).thenReturn(consumer);

    mockMvc.perform(post("/consumers")
            .contentType("application/json")
            .content("{\"name\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}"))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldGetConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    Consumer consumer = new Consumer(name);

    when(consumerService.findById(1L)).thenReturn(Optional.of(consumer));

    mockMvc.perform(get("/consumers/1"))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404ForUnknownConsumer() throws Exception {
    when(consumerService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/999"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldValidateOrderForConsumer() throws Exception {
    doNothing().when(consumerService).validateOrderForConsumer(eq(1L), any(Money.class));

    mockMvc.perform(post("/consumers/1/validate")
            .contentType("application/json")
            .content("{\"orderTotal\":\"100.00\"}"))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404WhenValidatingUnknownConsumer() throws Exception {
    doThrow(new ConsumerNotFoundException()).when(consumerService)
            .validateOrderForConsumer(eq(999L), any(Money.class));

    mockMvc.perform(post("/consumers/999/validate")
            .contentType("application/json")
            .content("{\"orderTotal\":\"100.00\"}"))
            .andExpect(status().isNotFound());
  }
}
