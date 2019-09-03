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
  }
}
```

In the above example, the form would be automatically instantiated, evaluated, 
and populated. Based on the presence of the `@JsonResponse` annotation, the
response would be a JSON error on validation failure. The annotation doesn't 
exist, but that's the idea. Some variation of that. And if you wanted to 
combine two forms, you could do the following:

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
  }
}
```

Same as before, but both forms get pre-processed. With the above syntax, 
Gemini gains the straight-to-parameters syntax of other modern libraries, while
keeping the concept of re-useable validation constraints.