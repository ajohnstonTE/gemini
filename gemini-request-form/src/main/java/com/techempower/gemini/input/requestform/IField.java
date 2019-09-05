package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.QueryValues;
import com.techempower.gemini.input.Values;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A field in a form. Associated with zero or more validators.
 *
 * @author ajohnston
 */
public interface IField<T>
{
  /**
   * Adds a validator to the field.
   *
   * @param validator the validator to add
   */
  IField<T> addValidator(Validator validator);

  /**
   * Adds a field validator to the field.
   *
   * @param fieldValidator the field validator to add
   */
  IField<T> addFieldValidator(FieldValidator<T> fieldValidator);

  /**
   * @return the name of the field
   */
  String getName();

  /**
   * @return true if the field must be specified by the user
   */
  boolean isRequired();

  /**
   * Sets whether or not the field must be specified by the user. If true, a
   * validator is automatically created when
   * needed.
   *
   * @param required whether or not the field is required
   */
  IField<T> setRequired(boolean required);

  /**
   * Provides the validators generated by the settings applied to this field,
   * such as required.
   *
   * @return the standard validators to apply
   */
  default List<Validator> getStandardValidators()
  {
    List<Validator> validators = new ArrayList<>();
    if (isRequired())
    {
      validators.add(new RequiredFieldValidator()
          .setField(this)
          .asValidator());
    }
    return validators;
  }

  /**
   * May return null if no validators required.
   *
   * @return all validators for this field
   */
  List<Validator> getValidators();
  
  /**
   * Sets the function used to extract a value from the query.
   */
  IField<T> setValueAccess(Function<ValueAccess, T> valueAccess);
  
  /**
   * Sets the function used to extract a value from the query. If no value is
   * found (null), default value is provided
   * instead.
   *
   * @see #setDefaultOnProcess(Object)
   */
  default IField<T> setValueAccess(Function<ValueAccess, T> valueAccess,
                                   T defaultValue)
  {
    setValueAccess(valueAccess);
    setDefaultOnProcess(defaultValue);
    return this;
  }
  
  /**
   * Sets the default value to set the field to during processing if no value
   * is provided (null by default).
   *
   * @param defaultOnProcess - the default value to use
   */
  IField<T> setDefaultOnProcess(T defaultOnProcess);
  
  /**
   * @return the default value to set the field to after processing if no value
   * is provided (null)
   */
  T getDefaultOnProcess();
  
  /**
   * Sets the current value of the field to the default value. This is a
   * convenience method to avoid having to specify the same value twice.
   */
  default IField<T> setValueToDefault()
  {
    setValue(getDefaultOnProcess());
    return this;
  }
  
  /**
   * Not (currently) to be used during validation.
   *
   * @return the current, stored value in the field
   */
  T getValue();
  
  /**
   * To be used during validation. Gets the value for the field using the given
   * input and its value access.
   *
   * @param input - the input to get the value from
   * @return the value derived from input using the value accessor.
   */
  default T getValueFrom(Input input)
  {
    Function<ValueAccess, T> valueAccess = getValueAccess();
    if (valueAccess != null)
    {
      T value = valueAccess.apply(new ValueAccess(
          new QueryValues(input), this));
      if (value != null)
      {
        return value;
      }
      else
      {
        return getDefaultOnProcess();
      }
    }
    return null;
  }

  IField<T> setValue(T value);
  
  default IField<T> setFrom(Values values)
  {
    {
      Function<ValueAccess, T> valueAccess = getValueAccess();
      if (valueAccess != null)
      {
        T value = valueAccess.apply(new ValueAccess(values, this));
        if (value != null)
        {
          setValue(value);
        }
        else
        {
          setValue(getDefaultOnProcess());
        }
      }
    }
    return this;
  }
  
  Function<ValueAccess, T> getValueAccess();

  /**
   * @return the type of value managed by this field
   */
  Class<T> getType();

  /**
   * @return validation specific to *only* this field. Can be used to determine
   * if any validation errors originated from this specific field. Note: This
   * must only be called after processing. This will return null otherwise.
   */
  Input input();

  /**
   * Used during validation to provide the semi-isolated input for the field
   *
   * @param inputToSyncOn - the input to sync against
   */
  IField<T> syncOnInput(Input inputToSyncOn);
}
