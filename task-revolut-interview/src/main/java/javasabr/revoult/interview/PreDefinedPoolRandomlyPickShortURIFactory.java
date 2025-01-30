package javasabr.revoult.interview;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PreDefinedPoolRandomlyPickShortURIFactory implements ShortURIFactory {

  private final List<URI> availableShortURIs = new ArrayList<>();

  public PreDefinedPoolRandomlyPickShortURIFactory(@NotNull Collection<URI> initial) {
    availableShortURIs.addAll(initial);
  }

  @Override
  public @NotNull URI generateFor(@NotNull URI original) {
    ThreadLocalRandom random = ThreadLocalRandom.current();

    while (true) {
      int possibleIndex = random.nextInt(0, availableShortURIs.size());
      URI result = tryToPickUp(possibleIndex);
      if (result != null) {
        return result;
      }
    }
  }

  private @Nullable URI tryToPickUp(int possibleIndex) {
    synchronized (availableShortURIs) {
      if (availableShortURIs.isEmpty()) {
        throw new IllegalStateException("No any available short URLs in pool");
      }
      if (availableShortURIs.size() <= possibleIndex) {
        return null;
      }
      return availableShortURIs.remove(possibleIndex);
    }
  }
}
