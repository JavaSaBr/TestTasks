package javasabr.device.rest.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConflictException extends ResponseStatusException {
  public ConflictException(@NotNull String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
