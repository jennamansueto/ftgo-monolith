package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.ActionDTO;
import net.chrisrichardson.ftgo.courierservice.api.AddActionsRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class CourierServiceClient {

  private RestTemplate restTemplate;
  private String courierServiceUrl;

  public CourierServiceClient(RestTemplate restTemplate, String courierServiceUrl) {
    this.restTemplate = restTemplate;
    this.courierServiceUrl = courierServiceUrl;
  }

  public List<Long> findAllAvailableIds() {
    String url = courierServiceUrl + "/couriers/available";
    ResponseEntity<List<Long>> response = restTemplate.exchange(
        url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
    return response.getBody();
  }

  public void addActions(long courierId, List<ActionDTO> actions) {
    String url = courierServiceUrl + "/couriers/" + courierId + "/actions";
    restTemplate.postForEntity(url, new AddActionsRequest(actions), Void.class);
  }
}
