package com.techempower.transaction;

public class TransactionalProperty<T>
  implements TransactionItem
{
  private T primaryValue;
  private InheritableThreadLocal<T> threadLocalValue = new InheritableThreadLocal<T>()
  {
    @Override
    protected T initialValue()
    {
      return TransactionalProperty.this.getPrimaryValue();
    }
  };

  public TransactionalProperty()
  {
  }

  public TransactionalProperty(T initialValue)
  {
    this.primaryValue = initialValue;
  }

  public T get()
  {
    return Transaction.getCurrent() != null
        ? getThreadLocalValue()
        : getPrimaryValue();
  }

  public void set(T value)
  {
    Transaction transaction = Transaction.getCurrent();
    if (transaction != null)
    {
      setThreadLocalValue(value);
      transaction.registerChange(this);
    }
    else
    {
      setPrimaryValue(value);
    }
  }

  @Override
  public void rollback()
  {
    getThreadLocal().remove();
  }

  @Override
  public void commit()
  {
    setPrimaryValue(getThreadLocalValue());
  }

  private T getPrimaryValue()
  {
    return primaryValue;
  }

  private void setPrimaryValue(T value)
  {
    primaryValue = value;
  }

  private T getThreadLocalValue()
  {
    return threadLocalValue.get();
  }

  private void setThreadLocalValue(T value)
  {
    threadLocalValue.set(value);
  }

  private InheritableThreadLocal<T> getThreadLocal()
  {
    return threadLocalValue;
  }
}
