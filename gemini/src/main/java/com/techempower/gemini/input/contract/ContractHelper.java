package com.techempower.gemini.input.contract;

import com.techempower.gemini.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides utility methods for contracts, several of which are duplicated from
 * {@link com.techempower.gemini.form.FormSelect FormSelect}.
 *
 * @author ajohnston
 */
public class ContractHelper
{
  public static ContractSelectField addOptionsForMonth(ContractSelectField formSelect, Context context)
  {
    return addOptionsFromResources(formSelect, context, "gfddmm");
  }
  
  /**
   * Reads a list of values and labels from the context's resources, given a
   * prefix.  Given the prefix "foo", the values would be loaded from
   * "foo-values" (comma-separated) and the labels would be loaded from
   * "foo-display" (separated by '#' characters).
   */
  public static ContractSelectField addOptionsFromResources(ContractSelectField formSelect, Context context, String prefix)
  {
    String[] resourcedValues = context.getResources().get(prefix + "-value").split(",");
    String[] resourcedLabels = context.getResources().get(prefix + "-display").split("#");
    int length = Math.min(resourcedValues.length, resourcedLabels.length);
    for (int i = 0; i < length; i++)
    {
      formSelect.addOption(resourcedValues[i], resourcedLabels[i]);
    }
    return formSelect;
  }
  
  /**
   * Adds a list of year &lt;options&lt; elements.
   *
   * @param negativeDelta Years prior to allow (e.g., 2).
   * @param positiveDelta Years in advance to allow (e.g., 10).
   */
  public static ContractSelectField addOptionsForYear(ContractSelectField formSelect, int negativeDelta, int positiveDelta)
  {
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int maxValue = currentYear + positiveDelta;
    int minValue = currentYear - negativeDelta;
    int length = maxValue - minValue + 1;
    for (int i = 0; i < length; i++)
    {
      formSelect.addOption("" + (minValue + i));
    }
    return formSelect;
  }
  
  public List<String> valueToList(Object value)
  {
    List<Object> valueObjects = new ArrayList<>();
    if (value != null)
    {
      if (value.getClass().isArray())
      {
        int length = Array.getLength(value);
        for (int i = 0; i < length; i++)
        {
          valueObjects.add(Array.get(value, i));
        }
      }
      else if (value instanceof Iterable)
      {
        for (Object o : ((Iterable) value))
        {
          valueObjects.add(o);
        }
      }
      else
      {
        valueObjects.add(value);
      }
    }
    return valueObjects
        .stream()
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .collect(Collectors.toList());
  }
}
