package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A field in a form. Associated with zero or more validators.
 */
public interface IField<T>
{
  /**
   * @return the name of the field
   */
  String getName();

  /**
   * TODO: Decide how this and the field validators will work out under the
   *  new structure. I don't like having both of these, I'd kinda like to
   *  squash validator into field validator, and only allow field-based
   *  validators. Not that it's super necessary though. Just an idea. I also
   *  don't want to break compatibility with the existing input stuff, so idk.
   *
   * TODO (follow-up): Sticking with the Input/Validator system may be
   *  limiting. Specifically, the input system is designed around the query
   *  params/form input, and around named element validation. But in the future
   *  this will support header fields, JSON, and whatever else. Things that
   *  100% won't be in the query. This kind of thing is covered by Values,
   *  though Values is currently just a way of accessing that data. A
   *  replacement for Input (like FieldInput) should be created that functions
   *  purely based on the generic Values data, while also being capable of
   *  directly accessing the field's getValue method if so desired (it would be
   *  more convenient when writing on-the-fly validators, currently this is
   *  done using Field#getValueFrom(Input)).
   *
   * Adds a validator to the field.
   *
   * @param validator the validator to add
   */
  IField<T> addValidator(Validator validator);

  /**
   * Adds a field validator to the field.
   *
   * @param fieldValidator the field validator to add
   */
  IField<T> addFieldValidator(IFieldValidator<T> fieldValidator);

  /**
   * TODO: Decide if isRequired/setRequired should be a base field thing.
   *   It only exists here for the html stuff/as a convenience, but for
   *   consistency it could just be done through RequiredValidator or
   *   RequiredFieldValidator in all non-HTML contexts. Could also revisit
   *   the idea of having validators that actually add attributes to fields,
   *   thus eliminating the need for specialized fields entirely. It's a
   *   possibility. Though SelectField would still be necessary. Hm...
   * @return true if the field must be specified by the user
   */
  boolean isRequired();

  /**
   * Sets whether or not the field must be specified by the user. If true, a
   * validator is automatically created when
   * needed.
   *
   * @param required whether or not the field is required
   */
  IField<T> setRequired(boolean required);

  /**
   * TODO: Somehow, eliminate this. I'd rather not have any "hidden"
   *  validators. This pretty much only exists for the specialized validators
   *  which should be overriding the default validators method to handle this
   *  themselves. It shouldn't be in the base class/interface.
   *
   * Provides the validators generated by the settings applied to this field,
   * such as required.
   *
   * @return the standard validators to apply
   */
  default List<Validator> getStandardValidators()
  {
    List<Validator> validators = new ArrayList<>();
    if (isRequired())
    {
      validators.add(new RequiredFieldValidator<T>()
          .asValidator(this));
    }
    return validators;
  }

  /**
   * May return null if no validators required.
   *
   * @return all validators for this field
   */
  List<Validator> getValidators();
  
  /**
   * Sets the default value to set the field to during processing if no value
   * is provided (null by default).
   *
   * @param defaultOnProcess the default value to use
   */
  IField<T> setDefaultOnProcess(T defaultOnProcess);
  
  /**
   * TODO: Rename. This is still a terrible name.
   *
   * @return the default value to set the field to after processing if no value
   * is provided (null)
   */
  T getDefaultOnProcess();
  
  /**
   * Sets the current value of the field to the default value. This is a
   * convenience method to avoid having to specify the same value twice.
   */
  default IField<T> setValueToDefault()
  {
    setValue(getDefaultOnProcess());
    return this;
  }
  
  /**
   * Not (currently) to be used during validation.
   *
   * @return the current, stored value in the field
   */
  T getValue();

  // TODO: Keep... probably? Fields have values, and can be processed. That's
  //  what defines a field.
  IField<T> setValue(T value);

  /**
   * TODO: This will still be necessary, but should just be a validation class.
   *   It won't need to be the full input in the end, and shouldn't be.
   *
   * @return validation specific to *only* this field. Can be used to determine
   * if any validation errors originated from this specific field. Note: This
   * must only be called after processing. This will return null otherwise.
   */
  Input input();

  void process(Input input);

  T getValueFrom(Input input);

  <V> IDerivedField<T, V> derive(Function<T, V> derivation);
}
