package com.techempower.gemini.input.requestform;

import com.techempower.gemini.Context;
import com.techempower.gemini.context.Query;
import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.Validator;

import java.util.List;
import java.util.Map;

/**
 * A collection of form fields and validators.
 */
public interface IRequestForm
{
  /**
   * @return all fields included in this form
   */
  List<IField<?>> getFields();
  
  /**
   * Adds a field to the form
   *
   * @param field the field to add
   */
  void addField(IField<?> field);
  
  /**
   * Adds a validator to the form
   *
   * @param validator the validator to add
   */
  void addValidator(Validator validator);
  
  /**
   * Applies the validators from the form and its fields, then sets the values of all the fields.
   */
  Input process(Context context);
  
  /**
   * Sets the values of the form's fields to the values in the query.
   *
   * @param query the source from which to set the values of the fields in this form
   */
  @Deprecated
  void setValuesFromQuery(Query query);
  
  /**
   * Sets the values of the form's fields to the values in the query.
   *
   * @param query the source from which to set the values of the fields in this form
   */
  void setValuesFromMap(Map<String, List<String>> query);
}
