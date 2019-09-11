package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.SyncedInput;

import java.util.function.Function;

public class DerivedField<R, T>
  extends ExtendableField<T, DerivedField<R, T>>
  implements IDerivedField<R, T>, IField<T>
{
  private final IField<R>       source;
  private final Function<R, T> derivation;

  public DerivedField(IField<R> source,
                      Function<R, T> derivation)
  {
    this.source = source;
    this.derivation = derivation;
    source.addDerivedField(this);
  }

  @Override
  public String getName()
  {
    return getSource().getName();
  }

  @Override
  public IField<R> getSource()
  {
    return source;
  }

  @Override
  protected SyncedInput createSyncedInput(Input input)
  {
    return new SyncedInput(input, true);
  }

  protected Function<R, T> getDerivation()
  {
    return derivation;
  }

  @Override
  protected void processSelf(SyncedInput syncedInput)
  {
    if (syncedInput.passed())
    {
      super.processSelf(syncedInput);
    }
  }

  @Override
  public T getValueFrom(Input input)
  {
    T value = getDerivation().apply(getSource().getValue());
    if (value == null)
    {
      return getDefaultOnProcess();
    }
    else
    {
      return value;
    }
  }
}
