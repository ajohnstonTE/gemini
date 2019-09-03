package com.techempower.helper;

import com.techempower.helper.NumberHelper;
import com.techempower.helper.StringHelper;
import com.techempower.util.IntRange;
import com.techempower.util.LongRange;

/**
 * BoxedNumberHelper provides helper functions for working with numbers and simple
 * mathematics, while still allowing for null-values.
 *
 * @see NumberHelper
 */
public class BoxedNumberHelper
{
  //
  // Static methods.
  //
  
  /**
   * Force a provided integer to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum.
   */
  public static Integer boundInteger(Integer toBound, IntRange range)
  {
    return boundInteger(toBound, range.min, range.max);
  }
  
  /**
   * Force a provided integer to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum. If minumum is
   * null, it defaults to {@link Integer#MIN_VALUE}. If maximum is null, it
   * defaults to {@link Integer#MAX_VALUE}. If toBound is null, returns null.
   */
  public static Integer boundInteger(Integer toBound, Integer minimum, Integer maximum)
  {
    if (toBound == null)
    {
      return null;
    }
    return NumberHelper.boundInteger(
        toBound,
        minimum != null ? minimum : Integer.MIN_VALUE,
        maximum != null ? maximum : Integer.MAX_VALUE);
  }
  
  /**
   * Force a provided long to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum.
   */
  public static Long boundLong(Long toBound, LongRange range)
  {
    return boundLong(toBound, range.min, range.max);
  }
  
  /**
   * Force a provided long to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum. If minumum is
   * null, it defaults to {@link Long#MIN_VALUE}. If maximum is null, it
   * defaults to {@link Long#MAX_VALUE}. If toBound is null, returns null.
   */
  public static Long boundLong(Long toBound, Long minimum, Long maximum)
  {
    if (toBound == null)
    {
      return null;
    }
    return NumberHelper.boundLong(
        toBound,
        minimum != null ? minimum : Long.MIN_VALUE,
        maximum != null ? maximum : Long.MAX_VALUE);
  }
  
  /**
   * Parses a String representation of a standard integer into a boxed
   * base-10 int.  In the event of a parsing problem, the default value will
   * be returned.  We avoid throwing an exception since improper String
   * representations into numbers are very common (i.e., improperly formatted
   * numbers are not "exceptional.")
   *   <p>
   * This is inspired by a similar method in Guava.
   */
  public static Integer parseInt(final String string, final Integer defaultValue)
  {
    // If the string is null or empty, return default.
    if (StringHelper.isEmpty(string))
    {
      return defaultValue;
    }
    
    // Check for a sign.
    final boolean negative = string.charAt(0) == '-';
    final int length = string.length();
    
    int index = negative ? 1 : 0;
    
    // Only a sign?  Default value.
    if (index == length)
    {
      return defaultValue;
    }
    
    // Compute digit by removing '0' from the character.
    int digit = string.charAt(index++) - '0';
    
    // Not a sign or digit?  Default value.
    if (  (digit < 0)
        || (digit > 9)
    )
    {
      return defaultValue;
    }
    
    int accumulator = -digit;
    int cap = Integer.MIN_VALUE / 10;
    
    while (index < length)
    {
      digit = string.charAt(index++) - '0';
      
      // Check for impending overflow.  If so, default value.
      if (  (digit < 0)
          || (digit > 9)
          || (accumulator < cap)
      )
      {
        return defaultValue;
      }
      
      // "Shift" the collected digits over to the left.
      accumulator *= 10;
      
      // Check for overflow.  Default value.
      if (accumulator < Integer.MIN_VALUE + digit)
      {
        return defaultValue;
      }
      
      // Apply the digit.
      accumulator -= digit;
    }
    
    // Apply the sign and return.
    if (negative)
    {
      return accumulator;
    }
    // If the accumulator is at the minimum value but the sign is positive,
    // that's the edge overflow case (positive range is one smaller than
    // negative range).  Return default value.
    else if (accumulator == Integer.MIN_VALUE)
    {
      return defaultValue;
    }
    else
    {
      return -accumulator;
    }
  }
  
  /**
   * A simplified version of parseInt (see above) that uses null as its default
   * value if the String is not an integer or empty.
   *
   * @param string the String to parse.  If null or empty, the default value
   *   will be returned.
   */
  public static Integer parseInt(final String string)
  {
    return parseInt(string, null);
  }
  
  /**
   * Variation of parseInt that will bound the resulting parsed integer by a
   * minimum and maximum.
   *
   * @param string the String to parse.  If null or empty, the default value
   *   will be returned.
   * @param defaultValue a default int value to return is the string parameter
   *   does not parse correctly.
   * @param minimum A minimum boundary to enforce.
   * @param maximum A maximum boundary to enforce.
   */
  public static Integer parseInt(final String string, final Integer defaultValue,
                             final Integer minimum, final Integer maximum)
  {
    return boundInteger(parseInt(string, defaultValue), minimum, maximum);
  }
  
  /**
   * Parses a String representation of a long integer into a boxed
   * base-10 long.  In the event of a parsing problem, the default value will
   * be returned.  We avoid throwing an exception since improper String
   * representations into numbers are very common (i.e., improperly formatted
   * numbers are not "exceptional.")
   *   <p>
   * This is inspired by a similar method in Guava.
   */
  public static Long parseLong(final String string, final Long defaultValue)
  {
    // If the string is null or empty, return default.
    if (StringHelper.isEmpty(string))
    {
      return defaultValue;
    }
    
    // Check for a sign.
    final boolean negative = string.charAt(0) == '-';
    final int length = string.length();
    
    int index = negative ? 1 : 0;
    
    // Only a sign?  Default value.
    if (index == length)
    {
      return defaultValue;
    }
    
    // Compute digit by removing '0' from the character.
    int digit = string.charAt(index++) - '0';
    
    // Not a sign or digit?  Default value.
    if (  (digit < 0)
        || (digit > 9)
    )
    {
      return defaultValue;
    }
    
    long accumulator = -digit;
    long cap = Long.MIN_VALUE / 10;
    
    while (index < length)
    {
      digit = string.charAt(index++) - '0';
      
      // Check for impending overflow.  If so, default value.
      if (  (digit < 0)
          || (digit > 9)
          || (accumulator < cap)
      )
      {
        return defaultValue;
      }
      
      // "Shift" the collected digits over to the left.
      accumulator *= 10;
      
      // Check for overflow.  Default value.
      if (accumulator < Long.MIN_VALUE + digit)
      {
        return defaultValue;
      }
      
      // Apply the digit.
      accumulator -= digit;
    }
    
    // Apply the sign and return.
    if (negative)
    {
      return accumulator;
    }
    // If the accumulator is at the minimum value but the sign is positive,
    // that's the edge overflow case (positive range is one smaller than
    // negative range).  Return default value.
    else if (accumulator == Long.MIN_VALUE)
    {
      return defaultValue;
    }
    else
    {
      return -accumulator;
    }
  }
  
  /**
   * A simplified version of parseLong (see above) that uses null as its default
   * value if the String is not a long or empty.
   *
   * @param string the String to parse.  If null or empty, the default value
   *   will be returned.
   */
  public static Long parseLong(final String string)
  {
    return parseLong(string, null);
  }
  
  /**
   * Variation of parseLong that will bound the resulting parsed long by a
   * minimum and maximum.
   *
   * @param string the String to parse.  If null or empty, the default value
   *   will be returned.
   * @param defaultValue a default long value to return is the string
   *   parameter does not parse correctly.
   * @param minimum A minimum boundary to enforce.
   * @param maximum A maximum boundary to enforce.
   */
  public static Long parseLong(final String string, final Long defaultValue,
                               final Long minimum, final Long maximum)
  {
    return boundLong(parseLong(string, defaultValue), minimum, maximum);
  }
  
  /**
   * Returns true if the given string represents a number, false otherwise.
   * This uses Integer.parseInt and relies on an exception being thrown to
   * indicate that the String is not a number.  We should eventually re-write
   * this method to not rely on an exception for a non-exceptional case.
   *   <p>
   */
  public static boolean isNumber(final String string)
  {
    return parseLong(string) != null;
  }  // End isNumber().
  
  /**
   * Does it's best to make an int out of what you give it. Uses
   * Double.parseDouble() and casts the result to an int.
   * If an Exception occurs then defaultValue is returned.
   *   <p>
   */
  public static Integer parseIntPermissive(String numStr, Integer defaultValue)
  {
    if (numStr != null)
    {
      try
      {
        return (int)Double.parseDouble(numStr);
      }
      catch (NumberFormatException nfe)
      {
        // Do nothing.
      }
    }
    return defaultValue;
  }
  
  /**
   * A pass-through to Float.parseFloat().
   * If a NumberFormatException occurs then defaultValue is returned.
   *   <p>
   * This method was formerly in StringHelper.
   */
  public static Float parseFloat(String numStr, Float defaultValue)
  {
    if (numStr != null)
    {
      try
      {
        return Float.parseFloat(numStr);
      }
      catch (NumberFormatException nfe)
      {
        // Do nothing.
      }
    }
    return defaultValue;
  }
  
  /**
   * A pass-through to Double.parseDouble().
   * If a NumberFormatException occurs then defaultValue is returned.
   */
  public static Double parseDouble(String numStr, Double defaultValue)
  {
    if (numStr != null)
    {
      try
      {
        return Double.parseDouble(numStr);
      }
      catch (NumberFormatException nfe)
      {
        // Do nothing.
      }
    }
    return defaultValue;
  }
  
  /**
   * A simple method to round a double to x number of decimal places.
   * If null is provided, null is returned.
   */
  public static Double round(Double value, int decimalPlaces)
  {
    if (value == null)
    {
      return null;
    }
    
    return NumberHelper.round(value, decimalPlaces);
  }
  
  /**
   * To address FindBugs rule FE_FLOATING_POINT_EQUALITY. Because floating
   * point calculations may involve rounding, calculated float and double
   * values may not be accurate. Instead we compare for equality within the
   * range of: ( Math.abs(a - b) < 0.0000001d ). Two nulls are considered equal.
   *
   * @param a value to compare
   * @param b value to compare
   * @return whether a and b are close enough to be considered equal
   */
  public static boolean almostEquals(Double a, Double b)
  {
    return almostEquals(a, b, 0.0000001d);
  }
  
  /**
   * To address FindBugs rule FE_FLOATING_POINT_EQUALITY. Because floating
   * point calculations may involve rounding, calculated float and double
   * values may not be accurate. Instead we compare for equality within the
   * range of: ( Math.abs(a - b) < epsilon ). Two nulls are considered equal.
   *
   * @param a value to compare
   * @param b value to compare
   * @param epsilon how close a and b need to be to be considered equal
   * @return whether a and b are close enough to be considered equal
   */
  public static boolean almostEquals(Double a, Double b, Double epsilon)
  {
    return (a == null && b == null) || (a != null && b != null && epsilon != null && Math.abs(a - b) < epsilon);
  }
  
  /**
   * You may not instantiate this class.
   */
  private BoxedNumberHelper()
  {
    // Does nothing.
  }
}
