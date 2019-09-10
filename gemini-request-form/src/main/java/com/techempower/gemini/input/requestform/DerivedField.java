package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;

import java.util.function.Function;

public class DerivedField<R, T>
  extends ExtendableField<T, DerivedField<R, T>>
  implements IDerivedField<R, T>, IField<T>
{
  private final Field<R>       source;
  private final Function<R, T> derivation;

  public DerivedField(Field<R> source,
                      Function<R, T> derivation)
  {
    this.source = source;
    this.derivation = derivation;
  }

  @Override
  public String getName()
  {
    return getSource().getName();
  }

  public Field<R> getSource()
  {
    return source;
  }

  protected Function<R, T> getDerivation()
  {
    return derivation;
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
