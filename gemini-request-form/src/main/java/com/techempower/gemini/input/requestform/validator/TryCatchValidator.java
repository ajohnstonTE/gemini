package com.techempower.gemini.input.requestform.validator;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.requestform.FieldValidator;

import java.util.function.Function;

public class TryCatchValidator<T>
  extends FieldValidator<T>
{
  private final Function<? super T, ?> func;
  private final String message;
  public TryCatchValidator(final Function<? super T, ?> func,
                           final String message)
  {
    this.func = func;
    this.message = message;
  }

  @Override
  protected void process(Input input)
  {
    try
    {
      func.apply(getValue(input));
    }
    catch (Exception e)
    {
      input.addError(getElementName(),
          String.format(message, getElementName()));
    }
  }
}
