package com.techempower.gemini.input.contract;

import com.techempower.gemini.Context;
import com.techempower.gemini.context.Query;
import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.Validator;

import java.util.List;
import java.util.Map;

/**
 * A collection of contract fields and validators.
 *
 * @author ajohnston
 */
public interface IContract
{
  /**
   * @return all fields included in this contract
   */
  List<IContractField<?>> getFields();
  
  /**
   * Adds a field to the contract
   *
   * @param field the field to add
   */
  void addField(IContractField<?> field);
  
  /**
   * Adds a validator to the contract
   *
   * @param validator the validator to add
   */
  void addValidator(Validator validator);
  
  /**
   * Applies the validators from the contract and its fields, then sets the values of all the fields.
   */
  Input process(Context context);
  
  /**
   * Sets the values of the contract's fields to the values in the query.
   *
   * @param query the source from which to set the values of the fields in this contract
   */
  void setValuesFromQuery(Query query);
  
  /**
   * Sets the values of the contract's fields to the values in the query.
   *
   * @param query the source from which to set the values of the fields in this contract
   */
  void setValuesFromMap(Map<String, List<String>> query);
}
