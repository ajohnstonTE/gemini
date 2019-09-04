package com.techempower.gemini.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicMutableValidation
  extends BasicValidation
  implements MutableValidation
{
  /**
   * Adds an error that is not tied to a specific element.
   *
   * @param errorMessage is typically a String, but can optionally be any
   *        other type that can be serialized to JSON and rendered in a
   *        template.
   */
  @Override
  public BasicMutableValidation addError(final String errorMessage)
  {
    if (errorMessages == null)
    {
      errorMessages = new ArrayList<>(10);
    }

    errorMessages.add(errorMessage);

    return this;
  }

  /**
   * Adds an element that is in error.
   *
   * @param element the element's identity string/name.
   * @param errorMessage A message describing the validation error.
   * @param addIfElementAlreadyPresent If the element is already in error
   *        for some other reason (already present in this Result), should
   *        this new message be added?
   */
  @Override
  public BasicMutableValidation addError(
      final String element,
      final String errorMessage,
      final boolean addIfElementAlreadyPresent)
  {
    if (erroredElements == null)
    {
      erroredElements = new HashMap<>(10);
    }

    // Capture single errors per element as plain Strings, and promote
    // multiple errors per element into Lists of Strings.
    final Object contained = erroredElements.get(element);
    if (contained == null)
    {
      erroredElements.put(element, errorMessage);
    }
    else if (  (contained instanceof String)
        && (addIfElementAlreadyPresent)
    )
    {
      final List<String> list = new ArrayList<>(10);
      list.add((String)contained);
      list.add(errorMessage);
      erroredElements.put(element, list);
    }
    else if (  (contained instanceof List<?>)
        && (addIfElementAlreadyPresent)
    )
    {
      @SuppressWarnings("unchecked")
      final List<String> list = (List<String>)contained;
      list.add(errorMessage);
    }

    addError(errorMessage);

    return this;
  }

  /**
   * Sets the HTTP status code to something specific.  The default is 400
   * ("bad request").
   */
  @Override
  public BasicMutableValidation setStatusCode(int statusCode)
  {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Set the auxiliary object reference.
   */
  @Override
  public BasicMutableValidation setAuxiliary(final Object auxiliary)
  {
    this.auxiliary = auxiliary;

    return this;
  }
}
