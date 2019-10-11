package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.helper.BoxedNumberHelper;

import java.util.Optional;

public abstract class NumberFieldRangeValidator<T>
  extends FieldValidator<T>
{
  T min;
  T max;
  
  public NumberFieldRangeValidator(T min, T max)
  {
    this.min = min;
    this.max = max;
  }
  
  protected String message()
  {
    String elementName = getElementName();
    T min = getMin();
    T max = getMax();
    if (min == null)
    {
      return elementName + " must be below or equal to " + max + ".";
    }
    else if (max == null)
    {
      return elementName + " must be above or equal to " + min + ".";
    }
    else
    {
       return elementName + " must be between " + min + " and " + max + ".";
    }
  }
  
  /**
   * @return the minimum
   */
  public T getMin()
  {
    return min;
  }

  /**
   * @return the maximum
   */
  public T getMax()
  {
    return max;
  }

  public static NumberFieldRangeValidator<Long> requireLong(Long min,
                                                            Long max)
  {
    return new LongRangeValidator(min, max);
  }

  public static NumberFieldRangeValidator<Double> requireDouble(Double min,
                                                                Double max)
  {
    return new DoubleRangeValidator(min, max);
  }

  public static NumberFieldRangeValidator<Float> requireFloat(Float min,
                                                              Float max)
  {
    return new FloatRangeValidator(min, max);
  }

  public static NumberFieldRangeValidator<Integer> requireInt(Integer min,
                                                              Integer max)
  {
    return new IntegerRangeValidator(min, max);
  }

  public static NumberFieldRangeValidator<Short> requireShort(Short min,
                                                              Short max)
  {
    return new ShortRangeValidator(min, max);
  }

  public static NumberFieldRangeValidator<Byte> requireByte(Byte min,
                                                            Byte max)
  {
    return new ByteRangeValidator(min, max);
  }
  
  private static class LongRangeValidator extends NumberFieldRangeValidator<Long>
  {
    LongRangeValidator(Long min, Long max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      final Long value = BoxedNumberHelper.parseLong(userValue, null);
      final Long minimum = Optional.ofNullable(getMin())
          .orElse(Long.MIN_VALUE);
      final Long maximum = Optional.ofNullable(getMax())
          .orElse(Long.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }
  
  private static class DoubleRangeValidator extends NumberFieldRangeValidator<Double>
  {
    DoubleRangeValidator(Double min, Double max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      final Double value = BoxedNumberHelper.parseDouble(userValue, null);
      final Double minimum = Optional.ofNullable(getMin())
          .orElse(Double.MIN_VALUE);
      final Double maximum = Optional.ofNullable(getMax())
          .orElse(Double.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }

  private static class FloatRangeValidator extends NumberFieldRangeValidator<Float>
  {
    FloatRangeValidator(Float min, Float max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      final Float value = Optional.ofNullable(BoxedNumberHelper
          .parseDouble(userValue, null))
          .map(Double::floatValue)
          .orElse(null);
      final Float minimum = Optional.ofNullable(getMin())
          .orElse(Float.MIN_VALUE);
      final Float maximum = Optional.ofNullable(getMax())
          .orElse(Float.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }

  private static class IntegerRangeValidator
      extends NumberFieldRangeValidator<Integer>
  {
    IntegerRangeValidator(Integer min, Integer max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      final Double value = Optional.ofNullable(BoxedNumberHelper
          .parseDouble(userValue, null))
          .orElse(null);
      final Integer minimum = Optional.ofNullable(getMin())
          .orElse(Integer.MIN_VALUE);
      final Integer maximum = Optional.ofNullable(getMax())
          .orElse(Integer.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }

  private static class ShortRangeValidator
      extends NumberFieldRangeValidator<Short>
  {
    ShortRangeValidator(Short min, Short max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      Short value;
      try
      {
        value = Short.parseShort(userValue);
      }
      catch (Exception e)
      {
        value = null;
      }

      final Short minimum = Optional.ofNullable(getMin())
          .orElse(Short.MIN_VALUE);
      final Short maximum = Optional.ofNullable(getMax())
          .orElse(Short.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }

  private static class ByteRangeValidator
      extends NumberFieldRangeValidator<Byte>
  {
    ByteRangeValidator(Byte min, Byte max)
    {
      super(min, max);
    }

    @Override
    protected void process(Input input)
    {
      String userValue = input.values().get(getElementName());
      Byte value;
      try
      {
        value = Byte.parseByte(userValue);
      }
      catch (Exception e)
      {
        value = null;
      }

      final Byte minimum = Optional.ofNullable(getMin())
          .orElse(Byte.MIN_VALUE);
      final Byte maximum = Optional.ofNullable(getMax())
          .orElse(Byte.MAX_VALUE);
      if (value == null || (value < minimum) || (value > maximum))
      {
        input.addError(getElementName(), message());
      }
    }
  }
}
