package com.techempower.gemini.input;

import com.techempower.gemini.context.Query;

/**
 * An abstraction/replacement of {@link Query} that allows for anything to serve as the base for the Query-like map.
 *
 * @author ajohnston
 */
public abstract class Values
{
  public abstract boolean has(String name);
  
  public String get(String name)
  {
    return get(name, null);
  }
  
  public abstract String get(String name, String defaultValue);
  
  /**
   * Get an array of Strings from the request.
   */
  public abstract String[] getStrings(String name, String[] defaultValue);
  
  /**
   * Get an array of Strings from the request.
   */
  public String[] getStrings(String name)
  {
    return getStrings(name, null);
  }
  
  /**
   * Gets an array of ints from the request.  Any non-numeric values will
   * be converted to 0.
   */
  public abstract int[] getInts(String name);
  
  /**
   * Gets an array of longs from the request.  Any non-numeric values will
   * be converted to 0.
   */
  public abstract long[] getLongs(String name);
  
  /**
   * Gets an enum request value.  If the HttpServletRequest returns null for
   * this parameter, or if the provided value is invalid, null will be returned.
   *
   * @param name the field to get the value of
   * @param type the type of the request parameter
   * @return the value as an enum.
   */
  public <O extends Enum<O>> O getEnum(String name, Class<O> type)
  {
    return getEnum(name, type, null);
  }
  
  /**
   * Gets an enum request value, with a default value provided.  If the
   * HttpServletRequest returns null for this parameter, or if the provided
   * value is invalid, the default will be returned.
   *
   * @param name         the name of the request parameter
   * @param type         the type of the request parameter
   * @param defaultValue a value to be returned if no value is provided by
   *                     the request, or if the provided value is invalid.
   * @return the value as an enum.
   */
  public abstract <O extends Enum<O>> O getEnum(String name, Class<O> type, O defaultValue);
}
