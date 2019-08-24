package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.validator.LongValidator;
import com.techempower.gemini.input.validator.NumericValidator;
import com.techempower.gemini.input.validator.Validator;

import java.util.List;

/**
 * A field that only allows whole numbers. Provides convenience methods for specifying the min/max values allowable.
 * If using the forms JSP tags, these are translated into the min/max attributes.
 *
 * @author ajohnston
 */
public class FormWholeNumberField<T>
    extends ExtendableFormField<T, FormWholeNumberField<T>>
{
  private Long min;
  private Long max;
  
  public FormWholeNumberField(IRequestForm contract, String name)
  {
    super(contract, name);
  }
  
  public Long getMin()
  {
    return min;
  }
  
  public FormWholeNumberField<T> setMin(Long min)
  {
    this.min = min;
    return this;
  }
  
  public Long getMax()
  {
    return max;
  }
  
  public FormWholeNumberField<T> setMax(Long max)
  {
    this.max = max;
    return this;
  }
  
  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = super.getStandardValidators();
    validators.add(new NumericValidator(getName()));
    Long min = getMin();
    Long max = getMax();
    if (min != null || max != null)
    {
      long minimum = min != null ? min : Long.MIN_VALUE;
      long maximum = max != null ? max : Long.MAX_VALUE;
      validators.add(new LongValidator(getName(), minimum, maximum));
    }
    return validators;
  }
}
