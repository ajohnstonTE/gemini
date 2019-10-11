package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;

/**
 * A validator to be applied for a form field. This field does not directly
 * extend validator as it is *not* intended to be added directly. Instead, the
 * field it is added to should provide a reference to itself, then convert it
 * to a validator. This is for the purpose of chain calls.
 */
public interface IFieldValidator<T>
{
  /**
   * Performs a validation of the element.  Returns null if there is no
   * validation error; a non-null String message otherwise.
   */
  void process(final IField<T> field, final Input input);
}
