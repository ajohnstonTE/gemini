package com.techempower.gemini.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.techempower.helper.CollectionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MutableValidation
  extends Validation
{

  /**
   * Adds an error that is not tied to a specific element.
   *
   * @param errorMessage is typically a String, but can optionally be any
   *        other type that can be serialized to JSON and rendered in a
   *        template.
   */
  MutableValidation addError(final String errorMessage);

  /**
   * Adds an element that is in error.  If an element is already in error
   * for another reason (already present in this Result), the new message
   * will still be added.
   *
   * @param errorMessage A message describing the validation error.
   */
  default MutableValidation addError(final String element, final String errorMessage)
  {
    return addError(element, errorMessage, true);
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
  MutableValidation addError(
      final String element,
      final String errorMessage,
      final boolean addIfElementAlreadyPresent);

  /**
   * Sets the HTTP status code to something specific.  The default is 400
   * ("bad request").
   */
  MutableValidation setStatusCode(int statusCode);

  /**
   * Set the auxiliary object reference.
   */
  MutableValidation setAuxiliary(final Object auxiliary);
}
