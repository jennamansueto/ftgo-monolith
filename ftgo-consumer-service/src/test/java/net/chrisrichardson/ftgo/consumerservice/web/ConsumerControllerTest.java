package net.chrisrichardson.ftgo.consumerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.common.PersonName;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConsumerControllerTest {

  private ConsumerService consumerService;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    consumerService = mock(ConsumerService.class);
    ConsumerController controller = new ConsumerController();

    // Use reflection to inject mock since ConsumerController uses @Autowired field injection
    try {
      java.lang.reflect.Field field = ConsumerController.class.getDeclaredField("consumerService");
      field.setAccessible(true);
      field.set(controller, consumerService);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setMessageConverters(converter).build();
  }

  @Test
  public void shouldValidateConsumerSuccessfully() throws Exception {
    long consumerId = 1L;
    Money orderTotal = new Money("100.00");

    doNothing().when(consumerService).validateOrderForConsumer(consumerId, orderTotal);

    mockMvc.perform(post("/consumers/" + consumerId + "/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateConsumerRequest(orderTotal))))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundWhenConsumerDoesNotExist() throws Exception {
    long consumerId = 999L;
    Money orderTotal = new Money("100.00");

    doThrow(new ConsumerNotFoundException()).when(consumerService).validateOrderForConsumer(eq(consumerId), any(Money.class));

    mockMvc.perform(post("/consumers/" + consumerId + "/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new ValidateConsumerRequest(orderTotal))))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetConsumerById() throws Exception {
    long consumerId = 1L;
    Consumer consumer = new Consumer(new PersonName("John", "Doe"));

    when(consumerService.findById(consumerId)).thenReturn(Optional.of(consumer));

    mockMvc.perform(get("/consumers/" + consumerId))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundForNonExistentConsumer() throws Exception {
    long consumerId = 999L;

    when(consumerService.findById(consumerId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/" + consumerId))
            .andExpect(status().isNotFound());
  }
}
