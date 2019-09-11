package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.SyncedInput;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A field in a contract. Associated with zero or more validators. Partially a
 * minimal version of legacy Gemini form elements, using the input system, but
 * with certain changes to allow for greater flexibility in certain areas. No
 * rendering included. Intended to be used in conjunction with the JSP forms
 * tags, though most likely compatible with any/all templating languages.
 */
public abstract class Field<T>
    implements IField<T>
{
  private List<DerivedField<T, ?>> derivedFields;
  private List<Validator>          customValidators;
  private boolean                  required;
  private T                        value;
  private T                        defaultOnProcess;
  private SyncedInput              input;

  protected Field()
  {
    derivedFields = new ArrayList<>();
    customValidators = new ArrayList<>();
  }

  public <V> DerivedField<T, V> derive(Function<T, V> derivation)
  {
    DerivedField<T, V> derivedField = new DerivedField<>(this, derivation);
    getDerivedFields().add(derivedField);
    return derivedField;
  }

  @Override
  public Field<T> addValidator(Validator validator)
  {
    getCustomValidators().add(validator);
    return this;
  }

  protected List<DerivedField<T, ?>> getDerivedFields()
  {
    return derivedFields;
  }

  @Override
  public Field<T> addFieldValidator(IFieldValidator<T> fieldValidator)
  {
    addValidator(input -> fieldValidator.process(this, input));
    return this;
  }

  /**
   * @return any/all custom validators added to the field externally
   */
  protected List<Validator> getCustomValidators()
  {
    return customValidators;
  }

  /**
   * Processes any/all fields derived from this one iff this field passed
   * validation. The input provided to the derived fields is the one processed
   * by this one.
   *
   * @param input - the input to process
   */
  protected void processDerivedIfValid(Input input)
  {
    if (input.passed())
    {
      getDerivedFields().forEach(derivedField -> derivedField.process(input));
    }
  }

  @Override
  public boolean isRequired()
  {
    return required;
  }

  @Override
  public Field<T> setRequired(boolean required)
  {
    this.required = required;
    return this;
  }

  @Override
  public List<Validator> getValidators()
  {
    List<Validator> validators = new ArrayList<>(getCustomValidators());
    List<Validator> standardValidators = getStandardValidators();
    if (standardValidators != null)
    {
      validators.addAll(standardValidators);
    }
    return validators;
  }

  @Override
  public Field<T> setDefaultOnProcess(T defaultOnProcess)
  {
    this.defaultOnProcess = defaultOnProcess;
    return this;
  }

  @Override
  public T getDefaultOnProcess()
  {
    return defaultOnProcess;
  }

  @Override
  public Field<T> setValueToDefault()
  {
    IField.super.setValueToDefault();
    return this;
  }

  @Override
  public T getValue()
  {
    return value;
  }

  @Override
  public Field<T> setValue(T value)
  {
    this.value = value;
    return this;
  }

  @Override
  public Input input()
  {
    return this.input;
  }

  @Override
  public void process(Input input)
  {
    // Note: It doesn't really need query here, just the validation.
    // TODO: I don't know what I was talking about in the above comment.
    SyncedInput syncedInput = new SyncedInput(input);
    // TODO: This should eventually just be Validation, not the whole Input.
    this.input = syncedInput;
    getValidators().forEach(validator -> validator.process(syncedInput));
    setValue(getValueFrom(syncedInput));
    processDerivedIfValid(syncedInput);
  }
}
