package javasabr.device.model;

import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TopologyNode(
    @NotNull NetworkDevice device,
    @NotNull Set<TopologyNode> children,
    int totalChildrenCount) {

  public static @NotNull TopologyNode of(
      @NotNull NetworkDevice device,
      @NotNull Set<TopologyNode> children) {

    int total = children.size() + children
        .stream()
        .mapToInt(TopologyNode::totalChildrenCount)
        .sum();

    return new TopologyNode(device, children, total);
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (another == null || getClass() != another.getClass()) {
      return false;
    }
    var that = (TopologyNode) another;
    return Objects.equals(device, that.device);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(device);
  }
}
