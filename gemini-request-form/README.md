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
```

And again, using request forms

```java
class BasicExampleHandler extends MethodUriHandler<Context>
{
  class ExampleForm extends RequestForm
  {
    Field<Long> entityId = new Field<>(this, "entity-id", Long.class)
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
    Field<Long> entityId = new Field<>(this, "entity-id", Long.class)
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
    Field<Long> entityId = new Field<>(this, "entity-id", Long.class)
      .setRequired(true)
      .addValidator(NumberFieldValidator.requireLong());
  }

  class SecondExampleForm extends RequestForm
  {
    Field<LocalDate> date = new Field<>(this, "date", LocalDate.class)
      .setValueAccess(values -> values.has() 
          ? LocalDate.parse(values.getString())
          : null)
      .addValidator(input -> {
        try
        {
          this.date.getValueFrom(input);
        }
        catch(Exception e)
        {
          input.addError(this.date.getName(), "`date` is not a valid date.")
        }
      });
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
keeping the concept of re-useable validation constraints.


### Very Distant Future Features

Eventually, once JSON support is added for requests, support for deconstructing 
and validating nested JSON objects will be added. Example:

```java
class DeconstructingJsonHandler extends MethodUriHandler<Context>
{
  class ExampleForm extends RequestForm
  {
    Field<String> someText = new Field<>(this, "some-text", String.class);
    ObjectField actionData = new ObjectField(this, "action-data") {
      Field<long[]> entityIds = new Field<>(objectField, "entity-ids", long[].class);
      Field<Boolean> delete = new Field<>(objectField, "delete", Boolean.class);
    };
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

As it's written above, it wouldn't actually compile because 
`form.actionData.entityIds` and `form.actionData.delete` wouldn't be 
resolvable. But that's the general idea, at least.