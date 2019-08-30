package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Values;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A field in a contract. Associated with zero or more validators. Partially a
 * minimal version of legacy Gemini form elements, using the input system, but
 * with certain changes to allow for greater flexibility in certain areas. No
 * rendering included. Intended to be used in conjunction with the JSP forms
 * tags, though most likely compatible with any/all templating languages.
 */
public class Field<T>
    implements IField<T>
{
  private String                   name;
  private Class<T>                 type;
  private List<Validator>          customValidators;
  private boolean                  required;
  private Function<ValueAccess, T> valueAccess;
  private T                        value;
  private T                        defaultOnProcess;
  private boolean                  multivalued;

  public Field(IRequestForm contract, String name, Class<T> type)
  {
    this.name = name;
    this.customValidators = new ArrayList<>();
    this.type = type;
    this.determineDefaultValueAccess();
    contract.addField(this);
  }

  @Override
  public Field<T> addValidator(Validator validator)
  {
    getCustomValidators().add(validator);
    return this;
  }

  @Override
  public Field<T> addFieldValidator(FieldValidator<T> fieldValidator)
  {
    addValidator(fieldValidator.setField(this).asValidator());
    return this;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Class<T> getType()
  {
    return type;
  }

  @Override
  public boolean isRequired()
  {
    return required;
  }

  @Override
  public Field<T> setRequired(boolean required)
  {
    this.required = required;
    return this;
  }

  @Override
  public boolean isMultivalued()
  {
    return multivalued;
  }

  @Override
  public Field<T> setMultivalued(boolean multivalued)
  {
    this.multivalued = multivalued;
    return this;
  }

  @Override
  public List<Validator> getStandardValidators()
  {
    List<Validator> validators = new ArrayList<>();
    if (isRequired())
    {
      validators.add(new RequiredFieldValidator()
          .setField(this)
          .asValidator());
    }
    return validators;
  }

  @Override
  public List<Validator> getValidators()
  {
    List<Validator> validators = new ArrayList<>(getCustomValidators());
    List<Validator> standardValidators = getStandardValidators();
    if (standardValidators != null)
    {
      validators.addAll(standardValidators);
    }
    return validators;
  }

  @Override
  public Field<T> setValueAccess(Function<ValueAccess, T> valueAccess)
  {
    this.valueAccess = valueAccess;
    return this;
  }

  @Override
  public Field<T> setDefaultOnProcess(T defaultOnProcess)
  {
    this.defaultOnProcess = defaultOnProcess;
    return this;
  }

  @Override
  public T getDefaultOnProcess()
  {
    return defaultOnProcess;
  }

  @Override
  public T getValue()
  {
    return value;
  }

  @Override
  public Field<T> setValue(T value)
  {
    this.value = value;
    return this;
  }

  @Override
  public Function<ValueAccess, T> getValueAccess()
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

  @Override
  public Field<T> setValueAccess(Function<ValueAccess, T> valueAccess, T defaultValue)
  {
    IField.super.setValueAccess(valueAccess, defaultValue);
    return this;
  }

  @Override
  public Field<T> setValueToDefault()
  {
    IField.super.setValueToDefault();
    return this;
  }

  @Override
  public Field<T> setFrom(Values values)
  {
    IField.super.setFrom(values);
    return this;
  }

  @SuppressWarnings("unchecked")
  protected void determineDefaultValueAccess()
  {
    Class<T> type = getType();
    if (Long.class.equals(type))
    {
      setValueAccess(values -> (T)values.getLong());
    }
    else if (Integer.class.equals(type))
    {
      setValueAccess(values -> (T)values.getInt());
    }
    else if (Short.class.equals(type))
    {
      setValueAccess(values -> {
        Integer value = values.getInt();
        if (value != null)
        {
          return (T)(Short)value.shortValue();
        }
        return null;
      });
    }
    else if (Byte.class.equals(type))
    {
      setValueAccess(values -> {
        Integer value = values.getInt();
        if (value != null)
        {
          return (T)(Byte)value.byteValue();
        }
        return null;
      });
    }
    else if (Double.class.equals(type))
    {
      setValueAccess(values -> (T)values.getDouble());
    }
    else if (Float.class.equals(type))
    {
      setValueAccess(values -> {
        Double value = values.getDouble();
        if (value != null)
        {
          return (T)(Float)value.floatValue();
        }
        return null;
      });
    }
    else if (String.class.equals(type))
    {
      setValueAccess(values -> (T)values.getString());
    }
    else if (Boolean.class.equals(type))
    {
      setValueAccess(values -> (T)values.getBooleanLenient());
    }
    else if (String[].class.equals(type))
    {
      setValueAccess(values -> (T)values.getStrings());
    }
    else if (int[].class.equals(type))
    {
      setValueAccess(values -> (T)values.getInts());
    }
    else if (long[].class.equals(type))
    {
      setValueAccess(values -> (T)values.getLongs());
    }
  }
}
