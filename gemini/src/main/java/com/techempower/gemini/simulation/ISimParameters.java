package com.techempower.gemini.simulation;

import java.util.Iterator;
import java.util.function.BiConsumer;

/**
 * A definition of query parameters heavily based on the documentation for
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/URLSearchParams">URLSearchParams</a>
 * from MDN.
 */
public interface ISimParameters
{
  /**
   * Appends a specified key/value pair as a new search parameter.
   *
   * @param name  - The name of the parameter to append.
   * @param value - The value of the parameter to append.
   * @return a reference to this parameters object, for chaining
   */
  ISimParameters append(String name, String value);

  /**
   * Deletes the given search parameter, and its associated value,
   * from the list of all search parameters.
   *
   * @param name - The name of the parameter to be deleted.
   * @return a reference to this parameters object, for chaining
   */
  ISimParameters delete(String name);

  /**
   * @return an iterator allowing iteration through all key/value pairs
   * contained in this object.
   */
  Iterator<Entry> entries();

  /**
   * Allows iteration through all values contained in this object via a
   * callback function.
   *
   * @param consumer - A callback function that is executed against each
   *                 parameter, with the param value provided as its parameter.
   * @return a reference to this parameters object, for chaining
   */
  ISimParameters forEach(BiConsumer<String, String> consumer);

  /**
   * @param name - The name of the parameter to return.
   * @return A {@link String} if the given parameter is found; otherwise, null.
   */
  String get(String name);

  /**
   * @param name - The name of the parameter to return.
   * @return all the values associated with a given search parameter.
   */
  String[] getAll(String name);

  /**
   * @param name - The name of the parameter to find.
   * @return a {@link Boolean} indicating if such a given parameter exists.
   */
  boolean has(String name);

  /**
   * @return an iterator allowing iteration through all keys of the key/value
   * pairs contained in this object.
   */
  Iterator<String> keys();

  /**
   * Sets the value associated with a given search parameter to the given
   * value. If there are several values, the others are deleted.
   *
   * @param name  - The name of the parameter to set.
   * @param value - The value of the parameter to set.
   * @return a reference to this parameters object, for chaining
   */
  ISimParameters set(String name, String value);

  /**
   * @return an iterator allowing iteration through all values of the key/value
   * pairs contained in this object.
   */
  Iterator<String> values();

  /**
   * @return a string containing a query string suitable for use in a URL.
   */
  String toString();

  class Entry
  {
    private String name;
    private String value;

    Entry(String name, String value)
    {
      this.name = name;
      this.value = value;
    }

    public String getName()
    {
      return name;
    }

    protected Entry setName(String name)
    {
      this.name = name;
      return this;
    }

    public String getValue()
    {
      return value;
    }

    protected Entry setValue(String value)
    {
      this.value = value;
      return this;
    }
  }
}
