package com.techempower.transaction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Transaction
  implements TransactionItem
{
  private static InheritableThreadLocal<Transaction> CURRENT = new InheritableThreadLocal<>();
  private List<TransactionItem> itemsAffected;

  public static Transaction getCurrent()
  {
    return CURRENT.get();
  }

  public static void begin(Runnable action)
  {
    try
    {
      CURRENT.set(new Transaction());
      action.run();
      CURRENT.get().commit();
    }
    catch (Throwable e)
    {
      CURRENT.get().rollback();
      throw e;
    }
    finally
    {
      CURRENT.remove();
    }
  }

  protected void registerChange(TransactionItem transactionItem)
  {
    if (itemsAffected == null)
    {
      // TODO: Is this how I want to handle this? Or just a read-write lock?
      itemsAffected = new CopyOnWriteArrayList<>();
    }
    itemsAffected.add(transactionItem);
  }

  @Override
  public void rollback()
  {
    // TODO: This should probably be done synchronously in some way. See below.
    if (itemsAffected != null)
    {
      itemsAffected.forEach(TransactionItem::rollback);
    }
  }

  @Override
  public void commit()
  {
    // TODO: This will need to be done synchronously in some way. Whenever a
    //  transaction completes, all of those values should become locked while
    //  it copies things over, or it should "flip a switch" targeting the new
    //  values as a whole without blocking anything (probably just use locks
    //  for now, that'll be easier)
    if (itemsAffected != null)
    {
      itemsAffected.forEach(TransactionItem::commit);
    }
  }
}
