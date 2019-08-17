package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.ElementValidator;

/**
 * A validator to be applied for a contract field.
 *
 * @author ajohnston
 */
public abstract class FieldValidator<T>
    extends ElementValidator
{
  private IContractField<T> field;
  
  /**
   * Constructor.
   *
   * @param elementName
   */
  public FieldValidator(IContractField<T> field)
  {
    super(field.getName());
    this.field = field;
  }
  
  public IContractField<T> getField()
  {
    return field;
  }
  
  public T getValue(Input input)
  {
    return getField().getValueFrom(input);
  }
}
