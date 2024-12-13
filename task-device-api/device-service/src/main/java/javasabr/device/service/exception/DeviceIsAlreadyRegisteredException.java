package javasabr.device.service.exception;

public class DeviceIsAlreadyRegisteredException extends RuntimeException {
  public DeviceIsAlreadyRegisteredException(String message) {
    super(message);
  }
}
