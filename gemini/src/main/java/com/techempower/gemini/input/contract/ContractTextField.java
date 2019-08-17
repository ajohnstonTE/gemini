package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.validator.LengthValidator;
import com.techempower.gemini.input.validator.Validator;

import java.util.List;

/**
 * A field that supports text input. Provides convenience methods for specifying the min/max lengths values allowable.
 * If using the forms JSP tags, these are translated into the minlength/maxlength attributes.
 *
 * @author ajohnston
 */
public class ContractTextField<T>
    extends ExtendableContractField<T, ContractTextField<T>>
{
  private Integer minLength;
  private Integer maxLength;
  
  public ContractTextField(IContract contract, String name)
  {
    super(contract, name);
  }
  
  public Integer getMinLength()
  {
    return minLength;
  }
  
  public ContractTextField<T> setMinLength(Integer minLength)
  {
    this.minLength = minLength;
    return this;
  }
  
  public Integer getMaxLength()
  {
    return maxLength;
  }
  
  public ContractTextField<T> setMaxLength(Integer maxLength)
  {
    this.maxLength = maxLength;
    return this;
  }
  
  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = super.getStandardValidators();
    Integer minLength = getMinLength();
    Integer maxLength = getMaxLength();
    if (minLength != null || maxLength != null)
    {
      int minimumLength = minLength != null ? minLength : Integer.MIN_VALUE;
      int maximumLength = maxLength != null ? maxLength : Integer.MIN_VALUE;
      validators.add(new LengthValidator(getName(), minimumLength, maximumLength, !isRequired()));
    }
    return validators;
  }
}
