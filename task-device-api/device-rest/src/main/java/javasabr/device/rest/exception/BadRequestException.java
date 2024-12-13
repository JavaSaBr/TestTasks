package javasabr.device.rest.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
  public BadRequestException(@NotNull String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
