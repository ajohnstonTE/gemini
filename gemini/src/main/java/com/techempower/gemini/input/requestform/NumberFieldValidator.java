package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.helper.BoxedNumberHelper;
import com.techempower.helper.StringHelper;

import java.util.function.Function;

/**
 * Validates that the user provided string is a double within the provided
 * minimum and maximum.
 */
public abstract class NumberFieldValidator<T>
    extends FieldValidator<T>
{
  //
  // Methods.
  //

  @Override
  public void process(Input input)
  {
    String value = input.values().get(getElementName());
    boolean required = getField().isRequired();
    if (!StringHelper.isEmpty(value) || required)
    {
      if (!isValid(value))
      {
        input.addError(getElementName() + " is not a valid number.");
      }
    }
  }

  abstract boolean isValid(String value);

  public static NumberFieldValidator<Double> requireDouble()
  {
    return new DoubleValidator();
  }

  public static NumberFieldValidator<Float> requireFloat()
  {
    return new FloatValidator();
  }

  public static NumberFieldValidator<Integer> requireInt()
  {
    return new IntegerValidator();
  }

  public static NumberFieldValidator<Long> requireLong()
  {
    return new LongValidator();
  }

  public static NumberFieldValidator<Short> requireShort()
  {
    return new ShortValidator();
  }

  public static NumberFieldValidator<Byte> requireByte()
  {
    return new ByteValidator();
  }

  private static class DoubleValidator extends NumberFieldValidator<Double>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(valueIn -> BoxedNumberHelper
          .parseDouble(valueIn, null), value);
    }
  }

  private static class FloatValidator extends NumberFieldValidator<Float>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(valueIn -> BoxedNumberHelper
          .parseFloat(valueIn,null), value);
    }
  }

  private static class IntegerValidator extends NumberFieldValidator<Integer>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(BoxedNumberHelper::parseInt, value);
    }
  }

  private static class LongValidator extends NumberFieldValidator<Long>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(BoxedNumberHelper::parseLong, value);
    }
  }

  private static class ShortValidator extends NumberFieldValidator<Short>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(Short::parseShort, value);
    }
  }

  private static class ByteValidator extends NumberFieldValidator<Byte>
  {
    @Override
    boolean isValid(String value)
    {
      return tryParse(Byte::parseByte, value);
    }
  }

  private static boolean tryParse(Function<String, ?> parser,
                                            String input)
  {
    try
    {
      return parser.apply(input) != null;
    }
    catch (Exception e)
    {
      return false;
    }
  }
}
