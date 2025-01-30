package javasabr.revoult.interview;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;

public class RandomBasedShortURIFactory implements ShortURIFactory {

  private final AtomicLong idFactory = new AtomicLong(0);
  private final String basePrefix;

  public RandomBasedShortURIFactory(String basePrefix) {
    this.basePrefix = basePrefix;
  }

  @Override
  public @NotNull URI generateFor(@NotNull URI original) {
    return URI.create(basePrefix + idFactory.incrementAndGet());
  }
}
