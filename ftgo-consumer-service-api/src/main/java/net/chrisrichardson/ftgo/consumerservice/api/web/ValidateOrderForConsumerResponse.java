package net.chrisrichardson.ftgo.consumerservice.api.web;

public class ValidateOrderForConsumerResponse {
  private boolean valid;
  private String message;

  public ValidateOrderForConsumerResponse() {
  }

  public ValidateOrderForConsumerResponse(boolean valid) {
    this.valid = valid;
  }

  public ValidateOrderForConsumerResponse(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
