package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.helper.StringHelper;

/**
 * Validates that the expected user-provided value has, in fact, been
 * provided.
 */
public class RequiredFieldValidator
    extends FieldValidator
{
  /**
   * Constructor.
   */
  public RequiredFieldValidator()
  {
  }

  @Override
  protected FieldValidator setField(IFormField field)
  {
    super.setField(field);
    return this;
  }

  @Override
  public void process(final Input input)
  {
    Object value = getValue(input);
    if (value == null || (value instanceof String && StringHelper.isEmpty((String) value)))
    {
      input.addError(getElementName(), getElementName() + " is required.");
    }
  }
}
