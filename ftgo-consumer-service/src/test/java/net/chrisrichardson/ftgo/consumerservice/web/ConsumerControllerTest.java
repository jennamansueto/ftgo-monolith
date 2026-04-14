package net.chrisrichardson.ftgo.consumerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerNotFoundException;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.domain.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(consumerController)
            .setMessageConverters(converter)
            .build();

    // Inject mock via reflection since the controller uses @Autowired field injection
    try {
      java.lang.reflect.Field field = ConsumerController.class.getDeclaredField("consumerService");
      field.setAccessible(true);
      field.set(consumerController, consumerService);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void shouldCreateConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    Consumer consumer = new Consumer(name);
    // Set id via reflection since JPA @GeneratedValue won't run outside persistence context
    java.lang.reflect.Field idField = Consumer.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(consumer, 1L);

    when(consumerService.create(any(PersonName.class))).thenReturn(consumer);

    mockMvc.perform(post("/consumers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateConsumerRequest(name))))
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
  public void shouldReturn404ForNonExistentConsumer() throws Exception {
    when(consumerService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/999"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldValidateConsumer() throws Exception {
    Money orderTotal = new Money("100.00");

    doNothing().when(consumerService).validateOrderForConsumer(eq(1L), any(Money.class));

    mockMvc.perform(post("/consumers/1/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateConsumerRequest(orderTotal))))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404WhenValidatingNonExistentConsumer() throws Exception {
    Money orderTotal = new Money("100.00");

    doThrow(new ConsumerNotFoundException()).when(consumerService)
            .validateOrderForConsumer(eq(999L), any(Money.class));

    mockMvc.perform(post("/consumers/999/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateConsumerRequest(orderTotal))))
            .andExpect(status().isNotFound());
  }
}
