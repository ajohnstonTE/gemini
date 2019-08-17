package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.Values;
import com.techempower.helper.BoxedBooleanHelper;
import com.techempower.helper.BoxedNumberHelper;

/**
 * Provides a streamlined means to access the value(s) of a field in a query without the need to specify the name each
 * time.
 *
 * @author ajohnston
 */
public class ContractFieldValues
{
  private Values            values;
  private IContractField<?> field;
  
  public ContractFieldValues(Values values, IContractField<?> field)
  {
    this.values = values;
    this.field = field;
  }
  
  protected Values getValues()
  {
    return values;
  }
  
  protected IContractField<?> getField()
  {
    return field;
  }
  
  public boolean has()
  {
    return getValues().has(getField().getName());
  }
  
  public String getString()
  {
    return getValues().get(getField().getName());
  }
  
  public String getString(String defaultValue)
  {
    return getValues().get(getField().getName(), defaultValue);
  }
  
  /**
   * Get an array of Strings from the request.
   */
  public String[] getStrings(String[] defaultValue)
  {
    String[] values = getValues().getStrings(getField().getName());
    return values != null ? values : defaultValue;
  }
  
  /**
   * Get an array of Strings from the request.
   */
  public String[] getStrings()
  {
    return getStrings(null);
  }
  
  /**
   * Gets an array of ints from the request.  Any non-numeric values will
   * be converted to 0.
   */
  public int[] getInts(int[] defaultValue)
  {
    int[] values = getValues().getInts(getField().getName());
    return values != null ? values : defaultValue;
  }
  
  /**
   * Gets an array of ints from the request.  Any non-numeric values will
   * be converted to 0. Defaults to null.
   */
  public int[] getInts()
  {
    return getInts(null);
  }
  
  /**
   * Gets an array of longs from the request.  Any non-numeric values will
   * be converted to 0.
   */
  public long[] getLongs(long[] defaultValue)
  {
    long[] values = getValues().getLongs(getField().getName());
    return values != null ? values : defaultValue;
  }
  
  /**
   * Gets an array of longs from the request.  Any non-numeric values will
   * be converted to 0. Defaults to null.
   */
  public long[] getLongs()
  {
    return getLongs(null);
  }
  
  public Integer getInt()
  {
    return getInt(null);
  }
  
  public Integer getInt(Integer defaultValue)
  {
    return BoxedNumberHelper.parseInt(getString(), defaultValue);
  }
  
  public Integer getInt(Integer defaultValue, Integer minimum, Integer maximum)
  {
    return BoxedNumberHelper.parseInt(getString(), defaultValue, minimum, maximum);
  }
  
  public Long getLong()
  {
    return getLong(null);
  }
  
  public Long getLong(Long defaultValue)
  {
    return BoxedNumberHelper.parseLong(getString(), defaultValue);
  }
  
  public Long getLong(Long defaultValue, Long minimum, Long maximum)
  {
    return BoxedNumberHelper.parseLong(getString(), defaultValue, minimum, maximum);
  }
  
  public Boolean getBoolean()
  {
    return getBoolean(null);
  }
  
  public Boolean getBoolean(Boolean defaultValue)
  {
    return BoxedBooleanHelper.parseBooleanStrict(getString(), defaultValue);
  }
  
  /**
   * Gets a boolean value leniently, allowing the value to be provided
   * as "true", "yes", "y", "on", or "1". If no value can be found, defaults to null.
   */
  public Boolean getBooleanLenient()
  {
    return getBooleanLenient(null);
  }
  
  /**
   * Gets a boolean value leniently, allowing the value to be provided
   * as "true", "yes", "y", "on", or "1".
   */
  public Boolean getBooleanLenient(Boolean defaultValue)
  {
    return BoxedBooleanHelper.parseBoolean(getString(), defaultValue);
  }
  
  /**
   * Gets an enum request value.  If the HttpServletRequest returns null for
   * this parameter, or if the provided value is invalid, null will be returned.
   *
   * @param type  the type of the request parameter
   * @return the value as an enum.
   */
  public <O extends Enum<O>> O getEnum(Class<O> type)
  {
    return getValues().getEnum(getField().getName(), type);
  }
  
  /**
   * Gets an enum request value, with a default value provided.  If the
   * HttpServletRequest returns null for this parameter, or if the provided
   * value is invalid, the default will be returned.
   *
   * @param type         the type of the request parameter
   * @param defaultValue a value to be returned if no value is provided by
   *                     the request, or if the provided value is invalid.
   * @return the value as an enum.
   */
  public <O extends Enum<O>> O getEnum(Class<O> type, O defaultValue)
  {
    return getValues().getEnum(getField().getName(), type, defaultValue);
  }
  
  /**
   * Gets a double request value, with a default value of zero.  If the
   * HttpServletRequest returns null for this parameter, null will
   * be returned.
   *
   * @return the value as a double.
   */
  public Double getDouble()
  {
    return getDouble(null);
  }
  
  /**
   * Gets a double request value, with a default value provided.  If the
   * HttpServletRequest returns null for this parameter, the default will
   * be returned.
   *
   * @param defaultValue a value to be returned if no value is provided by
   *                     the request.
   * @return the value as a double.
   */
  public Double getDouble(Double defaultValue)
  {
    return BoxedNumberHelper.parseDouble(getString(), defaultValue);
  }
}
