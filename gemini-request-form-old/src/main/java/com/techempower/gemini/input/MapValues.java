package com.techempower.gemini.input;

import com.techempower.helper.CollectionHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the {@link Values} class using a {@link Map}.
 */
public class MapValues
    extends Values
{
  private static final List<String>              EMPTY_LIST = Collections.emptyList();
  private              Map<String, List<String>> values;
  
  public MapValues(Map<String, List<String>> values)
  {
    this.values = new HashMap<>(values);
  }
  
  @Override
  public boolean has(String name)
  {
    return values.containsKey(name) && values.get(name) != null;
  }
  
  @Override
  public String get(String name, String defaultValue)
  {
    List<String> valuesAtKey = values.getOrDefault(name, EMPTY_LIST);
    String value = valuesAtKey.size() > 0 ? valuesAtKey.get(0) : null;
    return value != null ? value : defaultValue;
  }
  
  @Override
  public String[] getStrings(String name, String[] defaultValue)
  {
    List<String> valuesAtKey = values.getOrDefault(name, EMPTY_LIST);
    return valuesAtKey.size() > 0 ? valuesAtKey.toArray(new String[0]) : defaultValue;
  }
  
  @Override
  public int[] getInts(String name)
  {
    final String[] values = getStrings(name);
    return (values != null)
        ? CollectionHelper.toIntArray(values)
        : null;
  }
  
  @Override
  public long[] getLongs(String name)
  {
    final String[] values = getStrings(name);
    return (values != null)
        ? CollectionHelper.toLongArray(values)
        : null;
  }
  
  @Override
  public <O extends Enum<O>> O getEnum(String name, Class<O> type, O defaultValue)
  {
    final String value = get(name);
    if (value == null)
    {
      return defaultValue;
    }
    try
    {
      return Enum.valueOf(type, value);
    }
    catch (IllegalArgumentException e)
    {
      return defaultValue;
    }
  }
}
