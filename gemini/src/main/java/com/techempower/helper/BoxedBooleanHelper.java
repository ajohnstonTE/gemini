package com.techempower.helper;

public class BoxedBooleanHelper
{
  /**
   * Parses a String representing a boolean value. If the String does not
   * represent a valid boolean value then null is returned.
   *
   * This method will accept a number of common representations for booleans:
   * true, yes, 1, y, on
   * false, no, 0, n, off
   *
   * @param boolStr The String to parse.
   * @return The parsed boolean value.
   */
  public static Boolean parseBoolean(String boolStr)
  {
    return parseBoolean(boolStr, null);
  }
  
  /**
   * Parses a String representing a boolean value. If the String does not
   * represent a valid boolean value then the defaultValue is returned.
   *
   * This method will accept a number of common representations for booleans.
   *
   * @param boolStr The String to parse.
   * @return The parsed boolean value.
   */
  public static Boolean parseBoolean(String boolStr, Boolean defaultValue)
  {
    if (  (boolStr != null)
        && (boolStr.length() < 50)  // Long strings won't be evaluated.
    )
    {
      switch (boolStr.trim().toLowerCase())
      {
        case "true":
        case "yes":
        case "1":
        case "y":
        case "on":
          return true;
        case "false":
        case "no":
        case "0":
        case "n":
        case "off":
          return false;
      }
    }
  
    return defaultValue;
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
  
  /**
   * You may not instantiate this class.
   */
  private BoxedBooleanHelper()
  {
    // Does nothing.
  }
}
