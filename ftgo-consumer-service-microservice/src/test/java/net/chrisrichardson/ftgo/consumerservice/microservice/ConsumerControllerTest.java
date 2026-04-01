package net.chrisrichardson.ftgo.consumerservice.microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConsumerControllerTest {

  private MockMvc mockMvc;
  private ConsumerMicroservice consumerMicroservice;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() {
    consumerMicroservice = mock(ConsumerMicroservice.class);
    ConsumerController controller = new ConsumerController(consumerMicroservice);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void shouldCreateConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    ConsumerEntity consumer = new ConsumerEntity(name);

    when(consumerMicroservice.create(any(PersonName.class))).thenReturn(consumer);

    String requestBody = "{\"name\":{\"firstName\":\"John\",\"lastName\":\"Doe\"}}";

    mockMvc.perform(post("/consumers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldGetConsumer() throws Exception {
    PersonName name = new PersonName("John", "Doe");
    ConsumerEntity consumer = new ConsumerEntity(name);

    when(consumerMicroservice.findById(1L)).thenReturn(Optional.of(consumer));

    mockMvc.perform(get("/consumers/1"))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundForMissingConsumer() throws Exception {
    when(consumerMicroservice.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/consumers/999"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldValidateConsumerForOrder() throws Exception {
    doNothing().when(consumerMicroservice).validateOrderForConsumer(eq(1L), any(Money.class));

    String requestBody = "{\"orderTotal\":{\"amount\":100}}";

    mockMvc.perform(post("/consumers/1/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundWhenValidatingNonExistentConsumer() throws Exception {
    doThrow(new ConsumerNotFoundException()).when(consumerMicroservice)
            .validateOrderForConsumer(eq(999L), any(Money.class));

    String requestBody = "{\"orderTotal\":{\"amount\":100}}";

    mockMvc.perform(post("/consumers/999/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnUnprocessableEntityWhenValidationFails() throws Exception {
    doThrow(new ConsumerVerificationFailedException()).when(consumerMicroservice)
            .validateOrderForConsumer(eq(1L), any(Money.class));

    String requestBody = "{\"orderTotal\":{\"amount\":100}}";

    mockMvc.perform(post("/consumers/1/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isUnprocessableEntity());
  }
}
