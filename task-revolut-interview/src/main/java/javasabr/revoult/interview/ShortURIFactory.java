package javasabr.revoult.interview;

import java.net.URI;
import org.jetbrains.annotations.NotNull;

public interface ShortURIFactory {

  /**
   * @throws IllegalStateException if we cannot provide a short URI by some reason
   */
  @NotNull
  URI generateFor(@NotNull URI original) throws IllegalStateException;
}
