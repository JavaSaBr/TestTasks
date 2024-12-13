package javasabr.device.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum NetworkDeviceType {
  GATEWAY("Gateway"),
  SWITCH("Switch"),
  ACCESS_POINT("Access Point");

  private static final Map<String, NetworkDeviceType> LABEL_TO_ENUM = Arrays
      .stream(NetworkDeviceType.values())
      .collect(Collectors.toMap(NetworkDeviceType::label, Function.identity()));

  public static @Nullable NetworkDeviceType fromLabel(@NotNull String label) {
    return LABEL_TO_ENUM.get(label);
  }

  private final @NotNull String label;
}
