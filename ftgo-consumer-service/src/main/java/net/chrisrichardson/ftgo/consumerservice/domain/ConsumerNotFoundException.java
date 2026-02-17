package net.chrisrichardson.ftgo.consumerservice.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConsumerNotFoundException extends ConsumerVerificationFailedException {
}
