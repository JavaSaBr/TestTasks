package javasabr.device.rest.dto;

import java.util.List;
import javasabr.device.model.NetworkDevice;
import javasabr.device.model.NetworkDeviceType;
import javasabr.device.model.TopologyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TopologyNodeDto(
    @NotNull String macAddress,
    @NotNull String type,
    @Nullable List<TopologyNodeDto> children) {

  public static @NotNull TopologyNodeDto from(@NotNull TopologyNode node) {

    NetworkDevice device = node.device();
    NetworkDeviceType deviceType = device.type();

    List<TopologyNodeDto> children = node
        .children()
        .stream()
        .map(TopologyNodeDto::from)
        .toList();

    return new TopologyNodeDto(device.macAddress(), deviceType.label(), children.isEmpty() ? null : children);
  }
}
