# Gemini Framework Request Forms

Gemini request-form is an extension of the Gemini request input validation 
system that aims to provide static typing for request parameters, an enhanced 
level of reusability, and support for use as a view class when rendering 
templates such as Mustache or JSP.

### Example

```java
class BasicExampleHandler extends MethodUriHandler<Context>
{
  ValidatorSet validatorSet = new ValidatorSet(
    new RequiredValidator("entity-id"),
    new NumericValidator("entity-id")
  ); 

  // Input system
  @Path("input")
  public boolean handleInput()
  {
    Input input = validatorSet.process(context());
    if (input.failed())
    {
      // Add the errors to json
      return json();
    }
    long entityId = input.values().getLong("entity-id");
    // ...rest of the method...
  }
}
```

And again, using request forms

```java
class BasicExampleHandler extends MethodUriHandler<Context>
{
  class ExampleForm extends RequestForm
  {
    Field<Long> entityId = new BaseField<>(this, "entity-id", Long.class)
      .setRequired(true)
      .addValidator(NumberFieldValidator.requireLong());
  }

  // Request Form system
  @Path("request-form")
  public boolean handleRequestForm()
  {
    ExampleForm form = new ExampleForm();
    Input input = form.process(context());
    if (input.failed())
    {
      // Add the errors to json
      return json();
    }
    long entityId = form.entityId.getValue();
    // ...rest of the method...
  }
}
```

Ever-so-slightly longer, but with the benefit of static typing and 
use as a view class when templating (and more in the future).

### Future Features

Handler preprocessing via placement in method parameters:

```java
class PreprocessingHandler extends MethodUriHandler<Context>
{
  class ExampleForm extends RequestForm
  {
    Field<Long> entityId = new BaseField<>(this, "entity-id", Long.class)
      .setRequired(true)
      .addValidator(NumberFieldValidator.requireLong());
  }
  
  @Path("request-form")
  @JsonResponse
  public boolean handleRequestForm(ExampleForm form)
  {
    long entityId = form.entityId.getValue();
    // ...rest of the method...
  }
}
```

In the above example, the form would be automatically instantiated, evaluated, 
and populated. Based on the presence of the `@JsonResponse` annotation, the
response would be a JSON error on validation failure. It would be optional 
though. It essentially tells the handler how to respond if the `Accept` header 
isn't set. The annotation doesn't exist, but that's the idea. Some variation of
that. And if you wanted to combine two forms, you could do the following:

```java
class PreprocessingHandler extends MethodUriHandler<Context>
{
  class FirstExampleForm extends RequestForm
  {
    Field<Long> entityId = new BaseField<>(this, "entity-id", Long.class)
      .setRequired(true)
      .addValidator(NumberFieldValidator.requireLong());
  }

  class SecondExampleForm extends RequestForm
  {
    Field<LocalDate> date = new BaseField<>(this, "date", String.class)
      .addFieldValidator(new TryCatchValidator<>(LocalDate::parse, "`%s` is not a valid date"))
      .derive(LocalDate::parse);
  }
  
  @Path("request-form")
  @JsonResponse
  public boolean handleRequestForm(FirstExampleForm firstForm, 
                                   SecondExampleForm secondForm)
  {
    long entityId = firstForm.entityId.getValue();
    LocalDate date = secondForm.date.getValue();
    // ...rest of the method...
  }
}
```

Same as before, but both forms get pre-processed. With the above syntax, 
Gemini gains the straight-to-parameters syntax of other modern libraries, while
keeping the concept of re-usable validation constraints.


### Very Distant Future Features

Eventually, once JSON support is added for requests, support for deconstructing 
and validating nested JSON objects will be added. Example:

```java
class DeconstructingJsonHandler extends MethodUriHandler<Context>
{
  /* NestedField would implement both IRequestForm and IField */
  class ActionData extends NestedField 
  {
    Field<long[]> entityIds = new BaseField<>(this, "entity-ids", long[].class);
    Field<Boolean> delete = new BaseField<>(this, "delete", Boolean.class);
  }

  class ExampleForm extends RequestForm
  {
    Field<String> someText = new BaseField<>(this, "some-text", String.class);
    ActionData actionData = new ActionData(this, "action-data");
  }

  @Path("request-form")
  @JsonResponse
  public boolean handleRequestForm(ExampleForm form)
  {
    String someText = form.someText.getValue();
    long[] entityIds = form.actionData.entityIds.getValue();
    boolean delete = form.actionData.delete.getValue();
  }
}
``` 

The above would \*ideally\* be capable of deconstructing JSON that looked like:

```json
{
  "some-text": "foo",
  "action-data": {
    "entity-ids": [ 1, 2, 3 ],
    "delete": true
  }
}
```

That's the general idea, at least.

Less important features on the horizon include things like the processing of header values in fields, and the 
abstraction of field validation such that something like `LowercaseFieldValidator` could be applied to both a 
`HeaderField` and a normal query-based `Field` without otherwise needing to know where the values are coming from. 

Also, a move away from the standard query-based validators is being considered, in order to allow this to happen. 
On top of that, separating from the standard validation system will allow for general processing to be more 
abstractly controlled, which will be helpful when deserializing JSON or grouping array elements. Technically this
is all possible with the normal validation system, but by forcing all validation through a more general Input class,
the values provided can be redirected more easily and without it seeming like a hack.

Examples for that and a checklist for an "official" alpha release will come later.