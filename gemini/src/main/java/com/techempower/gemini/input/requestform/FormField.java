package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Values;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A field in a contract. Associated with zero or more validators. Partially a minimal version of legacy Gemini form
 * elements, using the input system, but with certain changes to allow for greater flexibility in certain areas. No
 * rendering included. Intended to be used in conjunction with the JSP forms tags, though most likely compatible with
 * any/all templating languages.
 *
 * @author ajohnston
 */
public class FormField<T>
    implements IFormField<T>
{
  private String                   name;
  private List<Validator>          customValidators;
  private boolean                  required;
  private Function<ValueAccess, T> valueAccess;
  private T                        value;
  private T                        defaultValue;
  private boolean                  multivalued;
  
  public FormField(IRequestForm contract, String name)
  {
    this.name = name;
    this.customValidators = new ArrayList<>();
    contract.addField(this);
  }
  
  @Override
  public FormField<T> addValidator(Validator validator)
  {
    getCustomValidators().add(validator);
    return this;
  }

  @Override
  public FormField<T> addFieldValidator(FieldValidator<T> fieldValidator)
  {
    addValidator(fieldValidator.setField(this).asValidator());
    return this;
  }
  
  @Override
  public String getName()
  {
    return name;
  }
  
  @Override
  public boolean isRequired()
  {
    return required;
  }
  
  @Override
  public FormField<T> setRequired(boolean required)
  {
    this.required = required;
    return this;
  }
  
  @Override
  public boolean isMultivalued()
  {
    return multivalued;
  }
  
  @Override
  public FormField<T> setMultivalued(boolean multivalued)
  {
    this.multivalued = multivalued;
    return this;
  }
  
  @Override
  public List<Validator> getStandardValidators()
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
  
  @Override
  public List<Validator> getValidators()
  {
    List<Validator> validators = new ArrayList<>();
    validators.addAll(getCustomValidators());
    List<Validator> standardValidators = getStandardValidators();
    if (standardValidators != null)
    {
      validators.addAll(standardValidators);
    }
    return validators;
  }
  
  @Override
  public FormField<T> setValueAccess(Function<ValueAccess, T> valueAccess)
  {
    this.valueAccess = valueAccess;
    return this;
  }
  
  @Override
  public FormField<T> setDefaultValue(T defaultValue)
  {
    this.defaultValue = defaultValue;
    return this;
  }
  
  @Override
  public T getDefaultValue()
  {
    return defaultValue;
  }
  
  @Override
  public T getValue()
  {
    return value;
  }
  
  @Override
  public FormField<T> setValue(T value)
  {
    this.value = value;
    return this;
  }
  
  @Override
  public Function<ValueAccess, T> getValueAccess()
  {
    return valueAccess;
  }
  
  /**
   * @return any/all custom validators added to the field externally
   */
  protected List<Validator> getCustomValidators()
  {
    return customValidators;
  }

  @Override
  public FormField<T> setValueAccess(Function<ValueAccess, T> valueAccess, T defaultValue)
  {
    IFormField.super.setValueAccess(valueAccess, defaultValue);
    return this;
  }

  @Override
  public FormField<T> setValueToDefault()
  {
    IFormField.super.setValueToDefault();
    return this;
  }

  @Override
  public FormField<T> setFrom(Values values)
  {
    IFormField.super.setFrom(values);
    return this;
  }
}
