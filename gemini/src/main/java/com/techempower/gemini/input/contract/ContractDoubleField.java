package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.validator.Validator;
import com.techempower.helper.StringHelper;

import java.util.List;

/**
 * A field that allows double-precision numbers. Provides convenience methods for specifying the min/max values
 * allowable. If using the forms JSP tags, these are translated into the min/max attributes.
 *
 * @author ajohnston
 */
public class ContractDoubleField<T>
    extends ContractField<T>
{
  private Double min;
  private Double max;
  
  public ContractDoubleField(IContract contract, String name)
  {
    super(contract, name);
  }
  
  public Double getMin()
  {
    return min;
  }
  
  public void setMin(Double min)
  {
    this.min = min;
  }
  
  public Double getMax()
  {
    return max;
  }
  
  public void setMax(Double max)
  {
    this.max = max;
  }
  
  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = super.getStandardValidators();
    validators.add((input -> {
      String value = input.values().get(getName());
      if (!StringHelper.isEmpty(value) || isRequired())
      {
        try
        {
          Double.parseDouble(value);
        }
        catch (Exception e)
        {
          input.addError(getName() + " is not a valid number.");
        }
      }
    }));
    Double min = getMin();
    Double max = getMax();
    if (min != null || max != null)
    {
      validators.add(new DoubleValidator(getName(), min, max));
    }
    return validators;
  }
}