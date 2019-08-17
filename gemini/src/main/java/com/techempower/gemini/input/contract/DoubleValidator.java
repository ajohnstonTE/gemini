package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.ElementValidator;
import com.techempower.helper.NumberHelper;

import java.util.Optional;

/**
 * Validates that the user provided string is a double within the provided
 * minimum and maximum.
 */
public class DoubleValidator
    extends ElementValidator
{
  
  //
  // Variables.
  //
  
  private final Double minimum;
  private final Double maximum;
  
  //
  // Methods.
  //
  
  /**
   * Constructor.
   */
  public DoubleValidator(String elementName, Double minimum, Double maximum)
  {
    super(elementName);
    this.minimum = minimum;
    this.maximum = maximum;
    if (minimum == null)
    {
      message(elementName + " must be below or equal to " + maximum + ".");
    }
    else if (maximum == null)
    {
      message(elementName + " must be above or equal to " + minimum + ".");
    }
    else
    {
      message(elementName + " must be between " + minimum + " and " + maximum + ".");
    }
  }
  
  /**
   * Constructor.
   */
  public DoubleValidator(String elementName, DoubleRange range)
  {
    this(elementName, range.min, range.max);
  }
  
  @Override
  public void process(final Input input)
  {
    final String userValue = getUserValue(input);
    final double doubleValue = NumberHelper.parseDouble(userValue, 0);
    final double minimum = Optional.ofNullable(getMinimum())
        .orElse(Double.MIN_VALUE);
    final double maximum = Optional.ofNullable(getMaximum())
        .orElse(Double.MAX_VALUE);
    if ((doubleValue < minimum) || (doubleValue > maximum))
    {
      input.addError(getElementName(), message);
    }
  }
  
  /**
   * @return the minimum
   */
  public Double getMinimum()
  {
    return minimum;
  }
  
  /**
   * @return the maximum
   */
  public Double getMaximum()
  {
    return maximum;
  }
  
}
