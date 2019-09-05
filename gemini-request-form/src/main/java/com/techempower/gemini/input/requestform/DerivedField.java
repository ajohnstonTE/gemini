package com.techempower.gemini.input.requestform;

import java.util.function.Function;

public class DerivedField<R, T>
  extends ExtendableField<T, DerivedField<R, T>>
{
  private final Field<R>       source;
  private final Function<R, T> derivation;

  public DerivedField(Field<R> source,
                      Class<T> type,
                      Function<R, T> derivation)
  {
    super(source.form(), source.getName(), type);
    this.source = source;
    this.derivation = derivation;
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
  public Function<ValueAccess, T> getValueAccess()
  {
    return source.getValueAccess().andThen(getDerivation());
  }
}
