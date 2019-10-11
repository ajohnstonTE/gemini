package com.techempower.requestform;

public interface IRequestForm
{
}

/*

```java

class BasicForm
  implements IRequestForm
{
  @Param("days")
  @ParamType(Integer.class)
  public final Field<Integer> days = new Field<>();

  @Param("user-id")
  @ParamType(Long.class) // May end up in Param. Indicates the base param type
  @Required               // Applies the required flag to *all* steps
  public final Field<User> user = new Field<Long>()
      .derive(UserStore.inst()::getUserById());

  @Param("name")
  @ParamType(String.class)
  public final Field<String> name = new Field<>()
      .setDefaultOnProcess("Jordan")
      .setValue("Alex");

  @Param("payment")
  @ParamType(String.class)
  public final Field<Float> payment = new Field<String>()
      .derive(CurrencyHelper::removeFormatting)
      .validate(new TryCatchValidator(Float::parseFloat, "%s is not a number"))
      .derive(Float::parseFloat);

  @Param("deposit")
  @MoneyParam(Double.class)  // This would be a custom annotation
  public final Field<Double> deposit = new Field<String>();

  @Param("deposit")
  public long count = 1;

  @Init    // Indicates that it should be provided on form init
  @Context // Likely defined in gemini-request-form, provides the context
  public Context context;

  @Param("name")
  @Required
  String name;

  @PathParam("another-id")
  @Optional               // Required will probably be applied by default
  public long anotherId;

  public static class Nested
  {
    @Param("closed")
    @BooleanDefault(true)      // Could also just use @Default("true")?
    public boolean closed;

    @Param("size")
    @ParamType(Integer.class)
    public Field<Integer> size;  // Field instantiations are optional

    @Param       // If not specified, params will assume the name of the field
    @ParamNaming(PropertyNamingStrategy.KEBAB_CASE)  // "last-name"
    public String lastName;
  }

  // The following only accepts JSON like:
  // {
  //   "list": [{
  //     "closed": false,
  //     "size": 50,
  //     "last-name": ""
  //   }, {
  //     "closed": null,
  //     "size": 2,
  //     "last-name": "Withers"
  //   }]
  // }
  @Param("list")
  List<Nested> someList;

  // The following would accept and aggregate form values like:
  // closed = false
  // size = 50
  // last-name =
  // closed =
  // size = 2
  // last-name = "Withers"
  @Zipped
  List<Nested> zippedList;

  // Note that both of the above should come out to the same value.
}

// Then, this could be used in any situation. By default, if using Param, the
// query params/form params are targeted. However, Param is intentionally
// generic. For example:

class Handler
{
  @Path("segment/{another-id}")
  public boolean handleIt(@ContentParams BasicForm)
  {
    // Grabs everything based on content-type (aside from the PathParam).
    // As such, @ContentParams is optional, this is the default behavior.
  }

  @Path("segment/{another-id}")
  public boolean handleItFromHeader(@HeaderParams BasicForm)
  {
    // Grabs everything from the header (aside from the PathParam).
  }

  @Path("segment/{another-id}")
  public boolean handleItDirectly(
      @PathParam("another-id") @Optional Long anotherId,
      @Param @MoneyParam(Double.class) Double deposit)
  {
    // Values directly provided.
  }
}

```

 */