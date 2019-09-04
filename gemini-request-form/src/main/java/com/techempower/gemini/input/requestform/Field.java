package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.*;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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
  private       String                   name;
  private       Class<T>                 type;
  private       List<Validator>          customValidators;
  private       boolean                  required;
  private       Function<ValueAccess, T> valueAccess;
  private       T                        value;
  private       T                        defaultOnProcess;
  private final IRequestForm             form;
  private       SyncedInput              input;

  public Field(IRequestForm form, String name, Class<T> type)
  {
    this.form = form;
    this.name = name;
    this.customValidators = new ArrayList<>();
    this.type = type;
    this.determineDefaultValueAccess();
    this.form().addField(this);
    this.input = null;
  }

  protected IRequestForm form()
  {
    return form;
  }

  public <V> DerivedField<T, V> derive(Class<V> type,
                                       Function<T, V> derivation)
  {
    DerivedField<T, V> field = new DerivedField<>(this, type, derivation);
    this.form().addField(field);
    return field;
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

  // Temporary implementation while I find a better way of doing this. Ideally shouldn't require two methods.
  public Field<T> addFieldValidator(BiConsumer<IField<T>, Input> fieldValidator)
  {
    return addFieldValidator(new FieldValidator<T>()
    {
      @Override
      protected void process(Input input)
      {
        fieldValidator.accept(getField(), input);
      }
    });
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
  public Input input()
  {
    return this.input;
  }

  protected Field syncOnInput(Input inputToSyncOn)
  {
    this.input = new SyncedInput(inputToSyncOn);
    return this;
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
  public Field<T> setValueAccess(Function<ValueAccess, T> valueAccess,
                                 T defaultValue)
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

  // TODO: For now it's fine here, but eventually move this to a strategy-type
  //  class. It's logic that doesn't *really* belong in this class.
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
