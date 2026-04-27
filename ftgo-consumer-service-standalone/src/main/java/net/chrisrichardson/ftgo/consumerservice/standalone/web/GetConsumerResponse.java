package net.chrisrichardson.ftgo.consumerservice.standalone.web;

import net.chrisrichardson.ftgo.common.PersonName;

public class GetConsumerResponse {
  private PersonName name;

  public PersonName getName() {
    return name;
  }

  public GetConsumerResponse(PersonName name) {
    this.name = name;
  }

  private GetConsumerResponse() {
  }
}
