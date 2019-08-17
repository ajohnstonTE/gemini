package com.techempower.gemini.input;

import com.techempower.gemini.Context;
import com.techempower.helper.CollectionHelper;

import java.util.*;

/**
 * A representation of input, composed of the user-provided values and any validation errors raised by validators. This
 * variation of Input is a combination of multiple inputs, combining any errors into a single input.
 *
 * @author ajohnston
 */
public class ComboInput
    extends Input
{
  private List<Input> inputs;
  
  /**
   * Constructor.
   *
   * @param context
   */
  public ComboInput(Context context)
  {
    super(context);
    inputs = new ArrayList<>();
  }
  
  public List<Input> getInputs()
  {
    return Collections.unmodifiableList(inputs());
  }
  
  public void addInput(Input input)
  {
    inputs().add(input);
  }
  
  protected List<Input> inputs()
  {
    return inputs;
  }
  
  @Override
  public List<String> errors()
  {
    List<String> allErrors = new ArrayList<>();
    Optional.ofNullable(super.errors())
        .ifPresent(allErrors::addAll);
    inputs()
        .stream()
        .map(Input::errors)
        .filter(Objects::nonNull)
        .forEachOrdered(allErrors::addAll);
    return allErrors;
  }
  
  @Override
  public Map<String, Object> erroredElements()
  {
    Map<String, Object> allErroredElements = new HashMap<>();
    Optional.ofNullable(super.erroredElements())
        .ifPresent(allErroredElements::putAll);
    inputs()
        .stream()
        .map(Input::erroredElements)
        .filter(Objects::nonNull)
        .forEachOrdered(allErroredElements::putAll);
    return allErroredElements;
  }
  
  @Override
  public boolean passed()
  {
    return super.passed() && inputs().stream().allMatch(Input::passed);
  }
  
  @Override
  public boolean failed()
  {
    return super.failed() && inputs().stream().anyMatch(Input::failed);
  }
  
  @Override
  public String toString()
  {
    return "Validation.Result ["
        + (passed() ? "Good; " : "Bad; ")
        + CollectionHelper.toString(errors(), ";")
        + "]";
  }
}
