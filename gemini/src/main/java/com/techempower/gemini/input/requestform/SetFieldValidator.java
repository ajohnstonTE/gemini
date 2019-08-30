package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates that the user-provided value is contained within a set of
 * permitted values. Supports multi-selected values. In these cases, will
 * verify that each of the user-provided values in contained within the
 * set of permitted values.
 */
public class SetFieldValidator<T>
    extends FieldValidator<T>
{
  
  //
  // Variables.
  //
  
  private final Set<String> permitted;
  
  //
  // Methods
  //
  
  /**
   * Constructor.
   */
  public SetFieldValidator(Object... validValues)
  {
    this.permitted = Arrays.stream(validValues)
        .map(String::valueOf)
        .collect(Collectors.toSet());
  }

  @Override
  public void process(final Input input)
  {
    final T actualValue = getValue(input);
    String message = getElementName() + " must be a permitted value.";
    if (actualValue != null && (actualValue instanceof Iterable ||
        actualValue.getClass().isArray()))
    {
      List<String> values = new RequestFormHelper().valueToList(actualValue);
      if (values.stream().anyMatch(value -> !permitted.contains(value)))
      {
        input.addError(getElementName(), message);
      }
      if (!getField().isMultivalued() && values.size() > 1)
      {
        input.addError(getElementName(), message);
      }
    }
    else
    {
      final String stringValue = String.valueOf(actualValue);
      if (actualValue != null || getField().isRequired())
      {
        if (!permitted.contains(stringValue))
        {
          input.addError(getElementName(), message);
        }
      }
    }
  }
  
}
