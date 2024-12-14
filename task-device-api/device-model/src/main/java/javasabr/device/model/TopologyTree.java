package javasabr.device.model;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

public record TopologyTree(@NotNull Set<TopologyNode> roots) {}
