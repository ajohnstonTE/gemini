package com.techempower.gemini.input.contract;

import com.techempower.gemini.input.Values;
import com.techempower.gemini.input.validator.Validator;

import java.util.function.Function;

public abstract class ExtendableContractField<T, S extends ExtendableContractField<T, S>>
  extends ContractField<T>
{

  public ExtendableContractField(IContract contract, String name)
  {
    super(contract, name);
  }

  @Override
  public S addValidator(Validator validator)
  {
    super.addValidator(validator);
    return self();
  }

  @Override
  public S setRequired(boolean required)
  {
    super.setRequired(required);
    return self();
  }

  @Override
  public S setMultivalued(boolean multivalued)
  {
    super.setMultivalued(multivalued);
    return self();
  }

  @Override
  public S setValueAccess(Function<ContractFieldValues, T> valueAccess)
  {
    super.setValueAccess(valueAccess);
    return self();
  }

  @Override
  public S setDefaultValue(T defaultValue)
  {
    super.setDefaultValue(defaultValue);
    return self();
  }

  @Override
  public S setValue(T value)
  {
    super.setValue(value);
    return self();
  }

  @Override
  public S setValueAccess(Function<ContractFieldValues, T> valueAccess, T defaultValue)
  {
    super.setValueAccess(valueAccess, defaultValue);
    return self();
  }

  @Override
  public S setValueToDefault()
  {
    super.setValueToDefault();
    return self();
  }

  @Override
  public S setFrom(Values values)
  {
    super.setFrom(values);
    return self();
  }

  @SuppressWarnings("unchecked")
  protected S self()
  {
    return (S)this;
  }
}
