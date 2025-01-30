package javasabr.revolut.interview;

import java.util.Currency;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Account {
  private AtomicLong availableBalance;
  private AtomicLong pendingToRemove;
  //long pendingToRemove;
  //long pendingToAdd;

  public boolean hasBalance(long toCheck) {
    if (toCheck < 1) {
      throw new IllegalArgumentException();
    }
    return availableBalance.get() >= toCheck;
  }

  public boolean tryToDecrease(long toDecrease) {
    if (toDecrease < 1) {
      throw new IllegalArgumentException();
    }
    while (true) {
      long current = availableBalance.get();
      if (toDecrease > current) {
        return false;
      }
      if (availableBalance.compareAndSet(current, current - toDecrease)) {
        //pendingToRemove.addAndGet(toDecrease);
        return true;
      }
    }
  }

  public boolean tryToIncrease(long toIncrease) {
    availableBalance.addAndGet(toIncrease);
    return true;
  }

  public void increaseBalance(long toAdd) {
    this.availableBalance.addAndGet(toAdd);
  }

  public void decreaseBalance(long toRemove) {
    this.availableBalance.set(availableBalance.get() - toRemove);
  }
}
