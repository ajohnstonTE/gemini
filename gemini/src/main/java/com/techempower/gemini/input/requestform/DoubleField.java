package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.validator.Validator;
import com.techempower.helper.StringHelper;

import java.util.List;

/**
 * A field that allows double-precision numbers. Provides convenience methods for specifying the min/max values
 * allowable. If using the forms JSP tags, these are translated into the min/max attributes.
 *
 * @author ajohnston
 */
public class DoubleField<T>
    extends ExtendableField<T, DoubleField<T>>
{
  private Double min;
  private Double max;
  
  public DoubleField(IRequestForm contract, String name)
  {
    super(contract, name);
  }
  
  public Double getMin()
  {
    return min;
  }
  
  public DoubleField<T> setMin(Double min)
  {
    this.min = min;
    return this;
  }
  
  public Double getMax()
  {
    return max;
  }
  
  public DoubleField<T> setMax(Double max)
  {
    this.max = max;
    return this;
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
