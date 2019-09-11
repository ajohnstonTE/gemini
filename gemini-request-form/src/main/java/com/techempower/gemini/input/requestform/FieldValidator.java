package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.Validator;

/**
 * A validator to be applied for a form field. This field does not directly
 * extend validator as it is *not* intended to be added directly. Instead, the
 * field it is added to should provide a reference to itself, then convert it
 * to a validator. This is for the purpose of chain calls.
 *
 * TODO: Add a method like `andThen(FieldValidator)` which would cause the
 *  validator to add the validator to its own set of follow up validators,
 *  which it would only process if it passed validation itself.
 */
public abstract class FieldValidator<T>
  implements IFieldValidator<T>
{
  //
  // Variables.
  //

  private IField<T> field;

  /**
   * Constructor.
   */
  public FieldValidator()
  {
  }

  /**
   * @param field - the field to associate with
   */
  private void setField(IField<T> field)
  {
    this.field = field;
  }
  
  protected IField<T> getField()
  {
    return field;
  }

  protected T getValue(Input input)
  {
    return getField().getValueFrom(input);
  }

  protected Validator asValidator(IField<T> field)
  {
    return input -> process(field, input);
  }

  /**
   * Performs a validation of the element.  Returns null if there is no
   * validation error; a non-null String message otherwise.
   */
  protected abstract void process(final Input input);

  @Override
  public void process(IField<T> field, Input input)
  {
    setField(field);
    process(input);
  }

  /**
   * Gets the Element's name.
   */
  public String getElementName()
  {
    return getField().getName();
  }
}
