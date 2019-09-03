package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.validator.Validator;

import java.util.List;

/**
 * A field that allows double-precision numbers. Provides convenience methods for specifying the min/max values
 * allowable. If using the forms JSP tags, these are translated into the min/max attributes.
 */
public class NumberField<T>
    extends ExtendableField<T, NumberField<T>>
{
  private T min;
  private T max;

  public NumberField(IRequestForm form, String name, Class<T> type)
  {
    super(form, name, type);
  }

  public T getMin()
  {
    return min;
  }

  public NumberField<T> setMin(T min)
  {
    this.min = min;
    return this;
  }

  public T getMax()
  {
    return max;
  }

  public NumberField<T> setMax(T max)
  {
    this.max = max;
    return this;
  }

  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = super.getStandardValidators();
    validators.add(getNumberValidator()
        .setField(this)
        .asValidator());
    T min = getMin();
    T max = getMax();
    if (min != null || max != null)
    {
      validators.add(getRangeValidator()
          .setField(this)
          .asValidator());
    }
    return validators;
  }

  @SuppressWarnings("unchecked")
  protected NumberFieldValidator<T> getNumberValidator()
  {
    Class<T> type = getType();
    if (Long.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireLong();
    }
    if (Integer.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireInt();
    }
    if (Double.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireDouble();
    }
    if (Float.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireFloat();
    }
    if (Short.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireShort();
    }
    if (Byte.class.equals(type))
    {
      return (NumberFieldValidator<T>) NumberFieldValidator.requireByte();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  protected NumberFieldRangeValidator<T> getRangeValidator()
  {
    Class<T> type = getType();
    if (Long.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireLong((Long) min, (Long) max);
    }
    if (Integer.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireInt((Integer) min, (Integer) max);
    }
    if (Double.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireDouble((Double) min, (Double) max);
    }
    if (Float.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireFloat((Float) min, (Float) max);
    }
    if (Short.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireShort((Short) min, (Short) max);
    }
    if (Byte.class.equals(type))
    {
      return (NumberFieldRangeValidator<T>) NumberFieldRangeValidator
          .requireByte((Byte) min, (Byte) max);
    }
    return null;
  }
}
