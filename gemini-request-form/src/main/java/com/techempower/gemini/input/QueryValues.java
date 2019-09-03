package com.techempower.gemini.input;

import com.techempower.gemini.context.Query;
import com.techempower.gemini.input.Input;

/**
 * An implementation of the {@link Values} class using {@link Query}.
 *
 * @author ajohnston
 */
public class QueryValues
    extends Values
{
  private Query query;
  
  public QueryValues(Query query)
  {
    this.query = query;
  }
  
  public QueryValues(Input input)
  {
    this(input.values());
  }
  
  protected Query getQuery()
  {
    return query;
  }
  
  @Override
  public boolean has(String name)
  {
    return getQuery().has(name);
  }
  
  @Override
  public String get(String name, String defaultValue)
  {
    return getQuery().get(name, defaultValue);
  }
  
  @Override
  public String[] getStrings(String name, String[] defaultValue)
  {
    String[] values = getQuery().getStrings(name);
    return values != null ? values : defaultValue;
  }
  
  @Override
  public int[] getInts(String name)
  {
    return getQuery().getInts(name);
  }
  
  @Override
  public long[] getLongs(String name)
  {
    return getQuery().getLongs(name);
  }
  
  @Override
  public <O extends Enum<O>> O getEnum(String name, Class<O> type, O defaultValue)
  {
    return getQuery().getEnum(name, type, defaultValue);
  }
}
