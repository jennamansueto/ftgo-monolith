package net.chrisrichardson.ftgo.consumerservice.standalone.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.web.ValidateOrderForConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.standalone.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.standalone.domain.ConsumerRepository;
import net.chrisrichardson.ftgo.consumerservice.standalone.domain.ConsumerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConsumerControllerTest {

  private ConsumerRepository consumerRepository;
  private ConsumerService consumerService;
  private ConsumerController consumerController;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    consumerRepository = mock(ConsumerRepository.class);
    consumerService = new ConsumerService(consumerRepository);
    consumerController = new ConsumerController(consumerService);

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(consumerController)
            .setMessageConverters(converter)
            .build();
  }

  private Consumer consumerWithId(long id, PersonName name) {
    Consumer consumer = new Consumer(name);
    try {
      Field idField = Consumer.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(consumer, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return consumer;
  }

  @Test
  public void shouldCreateConsumer() throws Exception {
    when(consumerRepository.save(any(Consumer.class))).thenAnswer(
            (InvocationOnMock invocation) -> {
              Consumer c = invocation.getArgument(0);
              return consumerWithId(1L, c.getName());
            });

    mockMvc.perform(post("/consumers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.consumerId").value(1));
  }

  @Test
  public void shouldGetConsumer() throws Exception {
    Consumer consumer = consumerWithId(1L, new PersonName("John", "Doe"));
    when(consumerRepository.findById(1L)).thenReturn(Optional.of(consumer));

    mockMvc.perform(get("/consumers/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name.firstName").value("John"));
  }

  @Test
  public void shouldReturn404WhenConsumerNotFound() throws Exception {
    when(consumerRepository.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/1"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldValidateOrderForConsumer() throws Exception {
    Consumer consumer = consumerWithId(1L, new PersonName("John", "Doe"));
    when(consumerRepository.findById(1L)).thenReturn(Optional.of(consumer));

    mockMvc.perform(post("/consumers/1/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new ValidateOrderForConsumerRequest(new Money("12.34")))))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturn404WhenValidatingNonExistentConsumer() throws Exception {
    when(consumerRepository.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(post("/consumers/999/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new ValidateOrderForConsumerRequest(new Money("12.34")))))
            .andExpect(status().isNotFound());
  }
}
