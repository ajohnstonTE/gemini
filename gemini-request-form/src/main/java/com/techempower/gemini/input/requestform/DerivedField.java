package com.techempower.gemini.input.requestform;

import java.util.function.Function;

public class DerivedField<R, T>
  extends ExtendableField<T, DerivedField<R, T>>
{
  private final Field<R> source;
  private final Function<R, T> deriver;

  public DerivedField(Field<R> source,
                      Class<T> type,
                      Function<R, T> deriver)
  {
    super(source.form(), source.getName(), type);
    this.source = source;
    this.deriver = deriver;
  }


}
