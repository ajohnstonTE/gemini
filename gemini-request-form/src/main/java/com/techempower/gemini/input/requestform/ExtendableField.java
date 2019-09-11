package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.Values;
import com.techempower.gemini.input.validator.Validator;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class ExtendableField<T, S extends ExtendableField<T, S>>
  extends Field<T>
{
  @Override
  public S addValidator(Validator validator)
  {
    super.addValidator(validator);
    return self();
  }

  @Override
  public S addFieldValidator(IFieldValidator<T> fieldValidator)
  {
    super.addFieldValidator(fieldValidator);
    return self();
  }

  @Override
  public S setRequired(boolean required)
  {
    super.setRequired(required);
    return self();
  }

  @Override
  public S setDefaultOnProcess(T defaultOnProcess)
  {
    super.setDefaultOnProcess(defaultOnProcess);
    return self();
  }

  @Override
  public S setValue(T value)
  {
    super.setValue(value);
    return self();
  }

  @Override
  public S setValueToDefault()
  {
    super.setValueToDefault();
    return self();
  }

  @SuppressWarnings("unchecked")
  protected S self()
  {
    return (S)this;
  }
}
