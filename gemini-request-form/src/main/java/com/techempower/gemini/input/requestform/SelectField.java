package com.techempower.gemini.input.requestform;

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
public class SelectField<T>
    extends ExtendableBaseField<T, SelectField<T>>
{
  private List<Option> options;
  private List<String> selectedValues;
  private Boolean allowMultipleValues = null;
  
  public SelectField(IRequestForm form, String name, Class<T> type)
  {
    super(form, name, type);
    options = new ArrayList<>();
  }
  
  public SelectField addOption(Option option)
  {
    options.add(option);
    option.setSelect(this);
    return this;
  }
  
  public SelectField addOption(Object value, String label)
  {
    return this.addOption(new Option(String.valueOf(value), label));
  }
  
  public SelectField addOption(String value)
  {
    return addOption(value, value);
  }
  
  public List<Option> getOptions()
  {
    return options;
  }
  
  @Override
  public SelectField<T> setValue(T value)
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
      setSelectedValues(new RequestFormHelper().valueToList(getValue()));
    }
    return selectedValues();
  }
  
  protected SelectField<T> setSelectedValues(List<String> selectedValues)
  {
    this.selectedValues = selectedValues;
    return this;
  }

  /**
   * Explicitly sets whether or not multiple values are allowed or not.
   */
  public SelectField<T> setAllowMultipleValues(boolean allowMultipleValues)
  {
    this.allowMultipleValues = allowMultipleValues;
    return this;
  }

  /**
   * If not specified by {@link #setAllowMultipleValues(boolean)}, is true if the managed type is either an array or is
   * a subclass of {@link Iterable}.
   *
   * @return whether or not multiple values can be specified for the field
   */
  public boolean isAllowMultipleValues()
  {
    if (allowMultipleValues != null)
    {
      return allowMultipleValues;
    }
    final Class<T> type = getType();
    return type.isArray() || Iterable.class.isAssignableFrom(type);
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
    validators.add(input -> {
      boolean allowMultipleValues = this.isAllowMultipleValues();
      if (!allowMultipleValues)
      {
        String[] values = input.values().getStrings(getName());
        if (values != null && values.length > 1)
        {
          input.addError(getName(), getName() + " may not have more than one value.");
        }
      }
    });
    validators.add(new SetFieldValidator<T>((Object[]) allOptions.toArray(String[]::new))
        .setField(this)
        .asValidator());
    return validators;
  }
  
  public static class Option
  {
    private SelectField<?> select;
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
    
    protected Option setSelect(SelectField<?> select)
    {
      this.select = select;
      return this;
    }
    
    protected SelectField<?> getSelect()
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
