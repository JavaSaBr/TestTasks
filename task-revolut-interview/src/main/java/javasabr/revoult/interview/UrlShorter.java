package javasabr.revoult.interview;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

public class UrlShorter {

  private final ConcurrentMap<URI, URI> shortToLong = new ConcurrentHashMap<>();
  private final AtomicInteger expectedSize = new AtomicInteger(0);
  private final ShortURIFactory shortURIFactory;
  private final int maxStoredSize;

  public UrlShorter(ShortURIFactory shortURIFactory, int maxStoredSize) {
    this.shortURIFactory = shortURIFactory;
    this.maxStoredSize = maxStoredSize;
  }

  @NotNull
  public URI generateShortVersion(@NotNull URI original) throws IllegalStateException {

    int sizeBeforePut = expectedSize.incrementAndGet();
    if (sizeBeforePut > maxStoredSize) {
      throw new IllegalStateException("The size of stored URLs has reached the maximum: " + maxStoredSize);
    }

    var shortUri = shortURIFactory.generateFor(original);
    shortToLong.put(shortUri, original);
    return shortUri;
  }

  @NotNull
  public URI resolveOriginal(@NotNull URI shortVersion) throws IllegalArgumentException {

    URI resolved = shortToLong.get(shortVersion);
    if (resolved == null) {
      throw new IllegalArgumentException("Unknown short URL: " + shortVersion);
    }

    return resolved;
  }
}
