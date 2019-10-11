package com.techempower.gemini.input.requestform;

import java.util.function.Function;

public class DefaultValueAccessStrategy
{
  @SuppressWarnings("unchecked")
  public <T> Function<ValueAccess, T> determineDefaultValueAccess(
      Class<T> type)
  {
    if (Long.class.equals(type))
    {
      return values -> (T) values.getLong();
    }
    else if (Integer.class.equals(type))
    {
      return values -> (T) values.getInt();
    }
    else if (Short.class.equals(type))
    {
      return values -> {
        Integer value = values.getInt();
        if (value != null)
        {
          return (T) (Short) value.shortValue();
        }
        return null;
      };
    }
    else if (Byte.class.equals(type))
    {
      return values -> {
        Integer value = values.getInt();
        if (value != null)
        {
          return (T) (Byte) value.byteValue();
        }
        return null;
      };
    }
    else if (Double.class.equals(type))
    {
      return values -> (T) values.getDouble();
    }
    else if (Float.class.equals(type))
    {
      return values -> {
        Double value = values.getDouble();
        if (value != null)
        {
          return (T) (Float) value.floatValue();
        }
        return null;
      };
    }
    else if (String.class.equals(type))
    {
      return values -> (T) values.getString();
    }
    else if (Boolean.class.equals(type))
    {
      return values -> (T) values.getBooleanLenient();
    }
    else if (String[].class.equals(type))
    {
      return values -> (T) values.getStrings();
    }
    else if (int[].class.equals(type))
    {
      return values -> (T) values.getInts();
    }
    else if (long[].class.equals(type))
    {
      return values -> (T) values.getLongs();
    }
    return null;
  }
}
