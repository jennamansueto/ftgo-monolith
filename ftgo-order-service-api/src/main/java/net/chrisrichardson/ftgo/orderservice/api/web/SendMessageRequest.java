package net.chrisrichardson.ftgo.orderservice.api.web;

public class SendMessageRequest {

  private String message;

  public SendMessageRequest() {
  }

  public SendMessageRequest(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
