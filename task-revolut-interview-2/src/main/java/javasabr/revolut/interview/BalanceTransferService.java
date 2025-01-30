package javasabr.revolut.interview;

import org.jetbrains.annotations.NotNull;

public class BalanceTransferService {

  public void transfer(@NotNull Account from, @NotNull Account to, long amount) {

    if(amount < 1) {
      throw new IllegalArgumentException();
    } else if(from == to) {
      throw new IllegalArgumentException();
    } else if (!from.tryToDecrease(amount)) {
      throw new IllegalStateException();
    }

    to.tryToIncrease(amount);
  }
}
