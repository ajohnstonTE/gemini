package com.techempower.helper;

public class BoxedBooleanHelper
{
  private BoxedBooleanHelper()
  {
  }

  /**
   * Parses a String representing a boolean value. If the String does not
   * represent a valid boolean value then the defaultValue is returned.
   * <p>
   * This method will accept a number of common representations for booleans.
   *
   * @param boolStr The String to parse.
   * @return The parsed boolean value.
   */
  public static Boolean parseBoolean(String boolStr, Boolean defaultValue)
  {
    try
    {
      return StringHelper.parseBoolean(boolStr);
    }
    catch (NumberFormatException e)
    {
      return defaultValue;
    }
  }

  /**
   * Strictly parses a String representing a boolean value, accepting only
   * "true" or "false", but permitting a default value if anything else is
   * provided.
   */
  public static Boolean parseBooleanStrict(String boolStr, Boolean defaultValue)
  {
    if ("false".equals(boolStr))
    {
      return false;
    }
    else if ("true".equals(boolStr))
    {
      return true;
    }
    else
    {
      return defaultValue;
    }
  }
}
