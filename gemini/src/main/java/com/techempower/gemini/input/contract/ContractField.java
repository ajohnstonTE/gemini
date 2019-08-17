package com.techempower.gemini.input.contract;

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
public class ContractField<T>
    implements IContractField<T>
{
  private String                           name;
  private List<Validator>                  customValidators;
  private boolean                          required;
  private Function<ContractFieldValues, T> valueAccess;
  private T                                value;
  private T                                defaultValue;
  private boolean                          multivalued;
  
  public ContractField(IContract contract, String name)
  {
    this.name = name;
    this.customValidators = new ArrayList<>();
    contract.addField(this);
  }
  
  @Override
  public void addValidator(Validator validator)
  {
    getCustomValidators().add(validator);
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
  public void setRequired(boolean required)
  {
    this.required = required;
  }
  
  @Override
  public boolean isMultivalued()
  {
    return multivalued;
  }
  
  @Override
  public void setMultivalued(boolean multivalued)
  {
    this.multivalued = multivalued;
  }
  
  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = new ArrayList<>();
    if (isRequired())
    {
      validators.add(new RequiredFieldValidator(this));
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
  public void setValueAccess(Function<ContractFieldValues, T> valueAccess)
  {
    this.valueAccess = valueAccess;
  }
  
  @Override
  public void setDefaultValue(T defaultValue)
  {
    this.defaultValue = defaultValue;
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
  public void setValue(T value)
  {
    this.value = value;
  }
  
  @Override
  public Function<ContractFieldValues, T> getValueAccess()
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
}