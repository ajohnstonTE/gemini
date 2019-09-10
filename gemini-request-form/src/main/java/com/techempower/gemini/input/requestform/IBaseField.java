package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.QueryValues;
import com.techempower.gemini.input.Values;

import java.util.function.Function;

public interface IBaseField<T>
  extends IField<T>
{
  /**
   * @return the type of value managed by this field
   */
  Class<T> getType();

  /**
   * Sets the function used to extract a value from the query.
   */
  IField<T> setValueAccess(Function<ValueAccess, T> valueAccess);

  /**
   * Sets the function used to extract a value from the query. If no value is
   * found (null), default value is provided
   * instead.
   *
   * @see #setDefaultOnProcess(Object)
   */
  default IField<T> setValueAccess(Function<ValueAccess, T> valueAccess,
                                   T defaultValue)
  {
    setValueAccess(valueAccess);
    setDefaultOnProcess(defaultValue);
    return this;
  }

  default IField<T> setFrom(Values values)
  {
    {
      Function<ValueAccess, T> valueAccess = getValueAccess();
      if (valueAccess != null)
      {
        T value = valueAccess.apply(new ValueAccess(values, this));
        if (value != null)
        {
          setValue(value);
        }
        else
        {
          setValue(getDefaultOnProcess());
        }
      }
    }
    return this;
  }

  /**
   * TODO: Remove after the processing changes are finished.
   *   Note: No longer sure if this is possible. If setFrom(Input)
   *   is moved to IField, then it is. But I don't want that. I think
   *   this is a better option. But it should probably be a protected
   *   method in AbstractField so that it can be used internally only.
   *
   * To be used during validation. Gets the value for the field using the given
   * input and its value access.
   *
   * @param input - the input to get the value from
   * @return the value derived from input using the value accessor.
   */
  default T getValueFrom(Input input)
  {
    Function<ValueAccess, T> valueAccess = getValueAccess();
    if (valueAccess != null)
    {
      T value = valueAccess.apply(new ValueAccess(
          new QueryValues(input), this));
      if (value != null)
      {
        return value;
      }
      else
      {
        return getDefaultOnProcess();
      }
    }
    return null;
  }

  Function<ValueAccess, T> getValueAccess();
}
