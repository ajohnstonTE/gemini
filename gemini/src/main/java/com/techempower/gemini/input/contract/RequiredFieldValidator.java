package com.techempower.gemini.input.contract;

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
  public RequiredFieldValidator(IContractField<?> field)
  {
    super(field);
    message(getField().getName() + " is required.");
  }
  
  @Override
  public void process(final Input input)
  {
    Object value = getValue(input);
    if (value == null || (value instanceof String && StringHelper.isEmpty((String) value)))
    {
      input.addError(getElementName(), getMessage());
    }
  }
}