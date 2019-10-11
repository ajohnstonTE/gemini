package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Values;

import java.util.function.Function;

public class BaseField<T>
    extends ExtendableField<T, BaseField<T>>
    implements IBaseField<T>
{
  private String                     name;
  private Class<T>                   type;
  private Function<ValueAccess, T>   valueAccess;
  private DefaultValueAccessStrategy defaultValueAccessStrategy;

  public BaseField(IRequestForm form, String name, Class<T> type)
  {
    this.name = name;
    this.type = type;
    this.determineDefaultValueAccess();
    form.addField(this);
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Class<T> getType()
  {
    return type;
  }

  @Override
  public BaseField<T> setValueAccess(Function<ValueAccess, T> valueAccess)
  {
    this.valueAccess = valueAccess;
    return this;
  }

  @Override
  public Function<ValueAccess, T> getValueAccess()
  {
    return valueAccess;
  }

  @Override
  public BaseField<T> setValueAccess(Function<ValueAccess, T> valueAccess,
                                     T defaultValue)
  {
    IBaseField.super.setValueAccess(valueAccess, defaultValue);
    return this;
  }

  @Override
  public BaseField<T> setFrom(Values values)
  {
    IBaseField.super.setFrom(values);
    return this;
  }

  protected void determineDefaultValueAccess()
  {
    Function<ValueAccess, T> valueAccess = new DefaultValueAccessStrategy()
        .determineDefaultValueAccess(getType());
    if (valueAccess != null)
    {
      setValueAccess(valueAccess);
    }
  }
}
