package com.techempower.gemini.params;

import com.google.common.net.UrlEscapers;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An extension of the defintion of {@link MultivaluedMap} intended to
 * preserve the original order of the elements, and better interface
 * with legacy Gemini code.
 */
public interface IParameters
  extends MultivaluedMap<String, String>
{
  /**
   * Allows iteration through all values contained in this object via a
   * callback function.
   *
   * @param consumer - A callback function that is executed against each
   *                 parameter, with the param value provided as its parameter.
   */
  void forEachFlat(BiConsumer<String, String> consumer);

  /**
   * @param name - The name of the parameter to return.
   * @return all the values associated with a given search parameter.
   */
  default String[] getAll(String name)
  {
    List<String> values = get(name);
    String[] arr = new String[0];
    return values != null ? values.toArray(arr) : arr;
  }

  /**
   * @return a map of the key value pairs. May be lossy. Necessary for legacy
   * compatibility.
   */
  default Map<String, String> compress()
  {
    Map<String, String> parameters = new HashMap<>();
    forEachFlat(parameters::putIfAbsent);
    return parameters;
  }

  /**
   * @return a string containing a query string suitable for use in a URL.
   */
  default String toQueryString()
  {
    if (isEmpty())
    {
      return "";
    }
    List<String> pairs = new ArrayList<>();
    forEachFlat((name, value) -> pairs.add(
        UrlEscapers.urlPathSegmentEscaper().escape(name) + "="
            + UrlEscapers.urlPathSegmentEscaper().escape(value)));
    return "?" + String.join("&", pairs);
  }
}
