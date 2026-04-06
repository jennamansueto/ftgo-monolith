package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerValidationException extends RuntimeException {

    public ConsumerValidationException(String message) {
        super(message);
    }

    public ConsumerValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
