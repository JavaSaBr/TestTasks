package javasabr.revoult.interview;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;

public class PreDefinedPoolShortURIFactory implements ShortURIFactory {

  private final Queue<URI> availableShortURIs = new ConcurrentLinkedQueue<>();

  public PreDefinedPoolShortURIFactory(@NotNull Collection<URI> initial) {
    availableShortURIs.addAll(initial);
  }

  @Override
  public @NotNull URI generateFor(@NotNull URI original) {
    try {
      return availableShortURIs.remove();
    } catch (NoSuchElementException e) {
      throw new IllegalStateException(e);
    }
  }
}
