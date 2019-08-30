package com.techempower.gemini.input.requestform;

import com.techempower.gemini.Context;
import com.techempower.gemini.context.Query;
import com.techempower.gemini.input.*;
import com.techempower.gemini.input.validator.Validator;

import java.util.*;
import java.util.stream.Stream;

/**
 * A collection of form fields, as well as custom validators. Partially a
 * minimal version of legacy Gemini forms, using the input system, but with
 * certain changes to allow for greater flexibility in certain areas. No
 * rendering included. Intended to be used in conjunction with the JSP forms
 * tags, though most likely compatible with any/all templating languages.
 */
public class RequestForm
    implements IRequestForm
{
  private List<IField<?>> fields;
  private List<Validator> customValidators;

  public RequestForm()
  {
    fields = new ArrayList<>();
    customValidators = new ArrayList<>();
  }

  @Override
  public List<IField<?>> getFields()
  {
    return new ArrayList<>(fields);
  }

  @Override
  public void addField(IField<?> field)
  {
    fields().add(field);
  }

  @Override
  public void addValidator(Validator validator)
  {
    customValidators().add(validator);
  }

  protected List<IField<?>> fields()
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
    return new ValidatorSet(Stream.concat(fieldValidators, customValidators)
        .toArray(Validator[]::new));
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
    setValuesFrom(new QueryValues(query));
  }

  @Override
  public void setValuesFromMap(Map<String, List<String>> query)
  {
    setValuesFrom(new MapValues(query));
  }

  protected void setValuesFrom(Values values)
  {
    getFields().forEach(field -> field.setFrom(values));
  }
}
