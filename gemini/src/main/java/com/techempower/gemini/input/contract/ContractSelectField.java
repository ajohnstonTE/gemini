package com.techempower.gemini.input.contract;

import com.techempower.collection.NamedValue;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A field that accepts lists of values. Provides convenience methods for specifying these options. If using the forms
 * JSP tags, this is best rendered as a select. Adds validators to ensure the selected values are all also added as
 * options.
 *
 * @author ajohnston
 */
public class ContractSelectField<T>
    extends ExtendableContractField<T, ContractSelectField<T>>
{
  private List<Option> options;
  private List<String> selectedValues;
  
  public ContractSelectField(IContract contract, String name)
  {
    super(contract, name);
    options = new ArrayList<>();
  }
  
  public ContractSelectField addOption(Option option)
  {
    options.add(option);
    option.setSelect(this);
    return this;
  }
  
  public ContractSelectField addOption(Object value, String label)
  {
    return this.addOption(new Option(String.valueOf(value), label));
  }
  
  public ContractSelectField addOption(String value)
  {
    return addOption(value, value);
  }
  
  public List<Option> getOptions()
  {
    return options;
  }
  
  @Override
  public ContractSelectField<T> setValue(T value)
  {
    super.setValue(value);
    // Reset the computed values list, as it is now outdated.
    setSelectedValues(null);
    return this;
  }
  
  public List<String> getSelectedValues()
  {
    if (selectedValues() == null)
    {
      setSelectedValues(new ContractHelper().valueToList(getValue()));
    }
    return selectedValues();
  }
  
  protected ContractSelectField<T> setSelectedValues(List<String> selectedValues)
  {
    this.selectedValues = selectedValues;
    return this;
  }
  
  protected List<String> selectedValues()
  {
    return selectedValues;
  }
  
  @Override
  public List<Validator> getStandardValidators()
  {
    Stream<String> valuableOptions = getOptions()
        .stream()
        .map(Option::getValue);
    Stream<String> emptyOption = Stream.of("");
    Stream<String> allOptions;
    if (!isRequired())
    {
      allOptions = Stream.concat(emptyOption, valuableOptions);
    }
    else
    {
      allOptions = valuableOptions;
    }
    List<Validator> validators = super.getStandardValidators();
    validators.add(new SetFieldValidator<T>((Object[]) allOptions.toArray(String[]::new))
        .setField(this)
        .asValidator());
    return validators;
  }
  
  public static class Option
  {
    private ContractSelectField<?> select;
    private String                  value;
    private String                  label;
    private HashMap<Object, Object> data;
    
    public Option(String value, String label)
    {
      this.value = value;
      this.label = label;
      this.data = new HashMap<>();
    }
    
    public Option(NamedValue namedValue)
    {
      this(namedValue.getValue(), namedValue.getName());
    }
    
    protected Option setSelect(ContractSelectField<?> select)
    {
      this.select = select;
      return this;
    }
    
    protected ContractSelectField<?> getSelect()
    {
      return select;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public String getLabel()
    {
      return label;
    }
    
    public Map<Object, Object> getData()
    {
      return data;
    }
    
    public boolean isSelected()
    {
      return getSelect() != null
          && getSelect().getSelectedValues().contains(getValue());
    }
    
    public boolean isValueIn(List<String> selectedValues)
    {
      return selectedValues.contains(getValue());
    }
  }
}
