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

/*  protected void validate()
  {

    if (true) throw new UnsupportedOperationException(
        "Fields need to be told validation is starting, then be given the" +
            " input being processed. This validator set method can pretty " +
            "much go away. Still, double check that it isn't used in TMPT.");

    Stream<Validator> fieldValidators = this.getFields()
        .stream()
        .flatMap(field -> Optional.ofNullable(field.getValidators())
            .map(Collection::stream)
            .orElseGet(Stream::of))
        .filter(Objects::nonNull);
    Stream<Validator> customValidators = this.customValidators().stream();
    *//*return*//* new ValidatorSet(Stream.concat(fieldValidators, customValidators)
        .toArray(Validator[]::new));
  }*/

  @Override
  public Input process(Context context)
  {
    // TODO: Eventually, all things (including fields) should be
    //  validators/validatable so that everything can be processed in the same
    //  order it was added to the form.
    // TODO: Rename "custom" validators to field/form validators.
    ValidatorSet validatorSet = new ValidatorSet(
        customValidators().toArray(new Validator[0]));
    Input formInput = validatorSet.process(context);
    getFields()
        .forEach(field -> {
          // TODO: Account for the fact that some derived fields might
          //  *somehow* be added before their source fields (probably not
          //  possible).
          // TODO: Eventually, find a way to not do this by type-checking each
          if (field instanceof DerivedField)
          {
            // TODO: The way this is written, a derived field that is dependent
            //  on another derived field will be processed if the root source
            //  field did not pass validation because the middle derived field
            //  will not have been evaluated. Address this.
            DerivedField derivedField = (DerivedField)field;
            if (derivedField.getSource().input().passed())
            {
              field.process(formInput);
            }
          }
          else
          {
            field.process(formInput);
          }
        });
    //setValuesFromQuery(context.query());
    return formInput;
  }

  @Override
  public void setValuesFromQuery(Query query)
  {
    throw new UnsupportedOperationException("Soon to be removed, most likely");
    //setValuesFrom(new QueryValues(query));
  }

  // TODO: Add documentation indicating this only does the base fields, not the
  //  derived. Also, make it only affect the base fields. I think. Otherwise,
  //  there would be no way to prevent exceptions (though exceptions *might* be
  //  acceptable to silence here. Maybe. Probably not).
  @Override
  public void setValuesFromMap(Map<String, List<String>> query)
  {
    setValuesFrom(new MapValues(query));
  }

  protected void setValuesFrom(Values values)
  {
    getFields()
        .stream()
        .filter(IBaseField.class::isInstance)
        .map(IBaseField.class::cast)
        .forEachOrdered(field -> field.setFrom(values));
  }
}
