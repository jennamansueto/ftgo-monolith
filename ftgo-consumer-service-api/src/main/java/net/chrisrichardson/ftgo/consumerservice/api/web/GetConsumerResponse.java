package net.chrisrichardson.ftgo.consumerservice.api.web;

import net.chrisrichardson.ftgo.common.PersonName;

public class GetConsumerResponse {
  private long consumerId;
  private PersonName name;

  private GetConsumerResponse() {
  }

  public GetConsumerResponse(long consumerId, PersonName name) {
    this.consumerId = consumerId;
    this.name = name;
  }

  public long getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(long consumerId) {
    this.consumerId = consumerId;
  }

  public PersonName getName() {
    return name;
  }

  public void setName(PersonName name) {
    this.name = name;
  }
}
