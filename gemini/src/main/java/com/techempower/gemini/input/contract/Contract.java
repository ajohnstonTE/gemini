package com.techempower.gemini.input.contract;

import com.techempower.gemini.Context;
import com.techempower.gemini.context.Query;
import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.MapValues;
import com.techempower.gemini.input.QueryValues;
import com.techempower.gemini.input.ValidatorSet;
import com.techempower.gemini.input.validator.Validator;

import java.util.*;
import java.util.stream.Stream;

/**
 * A collection of contract fields, as well as custom validators. Partially a minimal version of legacy Gemini forms,
 * using the input system, but with certain changes to allow for greater flexibility in certain areas. No rendering
 * included. Intended to be used in conjunction with the JSP forms tags, though most likely compatible with any/all
 * templating languages.
 *
 * @author ajohnston
 */
public class Contract
    implements IContract
{
  private List<IContractField<?>> fields;
  private List<Validator>         customValidators;
  
  public Contract()
  {
    fields = new ArrayList<>();
    customValidators = new ArrayList<>();
  }
  
  @Override
  public List<IContractField<?>> getFields()
  {
    return new ArrayList<>(fields);
  }
  
  @Override
  public void addField(IContractField<?> field)
  {
    fields().add(field);
  }
  
  @Override
  public void addValidator(Validator validator)
  {
    customValidators().add(validator);
  }
  
  protected List<IContractField<?>> fields()
  {
    return fields;
  }
  
  protected List<Validator> customValidators()
  {
    return customValidators;
  }
  
  protected ValidatorSet getValidatorSet()
  {
    Stream<Validator> fieldValidators = this.getFields()
        .stream()
        .flatMap(field -> Optional.ofNullable(field.getValidators())
            .map(Collection::stream)
            .orElseGet(Stream::of))
        .filter(Objects::nonNull);
    Stream<Validator> customValidators = this.customValidators().stream();
    return new ValidatorSet(Stream.concat(fieldValidators, customValidators).toArray(Validator[]::new));
  }
  
  @Override
  public Input process(Context context)
  {
    Input input = getValidatorSet().process(context);
    setValuesFromQuery(context.query());
    return input;
  }
  
  @Override
  public void setValuesFromQuery(Query query)
  {
    getFields().forEach(field -> field.setFrom(new QueryValues(query)));
  }
  
  @Override
  public void setValuesFromMap(Map<String, List<String>> query)
  {
    getFields().forEach(field -> field.setFrom(new MapValues(query)));
  }
}
