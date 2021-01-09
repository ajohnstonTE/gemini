package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class VariableReplacementConfigurationLoaderTest
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @RunWith(Parameterized.class)
  public static class load
  {
    @Parameterized.Parameters(name = "{0}")
    public static Object[][] getParams()
    {
      return new Object[][]{
          new Object[] {
              "basic replacement",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "basic replacement (boolean)",
              // language=JSON
              "{ \"a\": true, \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": true, \"b\": \"foo\", \"c\": true }",
          },
          new Object[] {
              "basic replacement (integer)",
              // language=JSON
              "{ \"a\": 3, \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": 3, \"b\": \"foo\", \"c\": 3 }",
          },
          new Object[] {
              "basic replacement (null)",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": null }",
          },
          new Object[] {
              "basic replacement (missing)",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"${d}\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": null }",
          },
          new Object[] {
              "escaped replacement marker",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"$${a}\" }",
              // For escapes, $${a} is translated to just ${a}
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"${a}\" }",
          },
          new Object[] {
              "escaped replacement marker (middle)",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"foo$${a}bar\" }",
              // For escapes, $${a} is translated to just ${a}
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"foo${a}bar\" }",
          },
          new Object[] {
              "path-field replacement",
              // language=JSON
              "{ \"a\": { \"x\": \"dog\" }, \"b\": \"foo\", \"c\": \"${a.x}\" }",
              // language=JSON
              "{ \"a\": { \"x\": \"dog\" }, \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "path-array replacement",
              // language=JSON
              "{ \"a\": [ \"dog\" ], \"b\": \"foo\", \"c\": \"${a[0]}\" }",
              // language=JSON
              "{ \"a\": [ \"dog\" ], \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "array replacement",
              // language=JSON
              "{ \"a\": [ \"dog\" ], \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": [ \"dog\" ], \"b\": \"foo\", \"c\": [ \"dog\" ] }",
          },
          new Object[] {
              "object replacement",
              // language=JSON
              "{ \"a\": { \"x\": \"dog\" }, \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": { \"x\": \"dog\" }, \"b\": \"foo\", \"c\": { \"x\": \"dog\" } }",
          },
          new Object[] {
              "substring replacement (start)",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"${a}, it is\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"dog, it is\" }",
          },
          new Object[] {
              "substring replacement (end)",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"It is a ${a}\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"It is a dog\" }",
          },
          new Object[] {
              "substring replacement (middle)",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"Is ${a} it?\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"Is dog it?\" }",
          },
          new Object[] {
              "substring replacement (middle, boolean)",
              // language=JSON
              "{ \"a\": true, \"b\": \"foo\", \"c\": \"Is ${a} it?\" }",
              // language=JSON
              "{ \"a\": true, \"b\": \"foo\", \"c\": \"Is true it?\" }",
          },
          new Object[] {
              "substring replacement (middle, null)",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": \"Is ${a} it?\" }",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": \"Is  it?\" }",
          },
          new Object[] {
              "substring replacement (middle, missing)",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": \"Is ${d} it?\" }",
              // language=JSON
              "{ \"a\": null, \"b\": \"foo\", \"c\": \"Is  it?\" }",
          },
          new Object[] {
              "substring replacement (top-down)",
              // language=JSON
              "{ \"a\": \"${c}\", \"b\": \"foo\", \"c\": \"dog\" }",
              // language=JSON
              "{ \"a\": \"dog\", \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "nested replacement",
              // language=JSON
              "{ \"a\": { \"x\": \"${b}\" }, \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": { \"x\": \"foo\" }, \"b\": \"foo\", \"c\": { \"x\": \"foo\" } }",
          },
          new Object[] {
              "nested replacement (reverse order)",
              // language=JSON
              "{ \"a\": \"${c}\", \"b\": \"foo\", \"c\": { \"x\": \"${b}\" } }",
              // language=JSON
              "{ \"a\": { \"x\": \"foo\" }, \"b\": \"foo\", \"c\": { \"x\": \"foo\" } }",
          },
          new Object[] {
              "nested replacement (array)",
              // language=JSON
              "{ \"a\": [ \"${b}\" ], \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": [ \"foo\" ], \"b\": \"foo\", \"c\": [ \"foo\" ] }",
          },
          new Object[] {
              "nested replacement (array, reverse order)",
              // language=JSON
              "{ \"a\": \"${c}\", \"b\": \"foo\", \"c\": [ \"${b}\" ] }",
              // language=JSON
              "{ \"a\": [ \"foo\" ], \"b\": \"foo\", \"c\": [ \"foo\" ] }",
          },
          new Object[] {
              "nested replacement (string)",
              // language=JSON
              "{ \"a\": \"${c} dog\", \"b\": \"foo\", \"c\": \"${b} bar\" }",
              // language=JSON
              "{ \"a\": \"foo bar dog\", \"b\": \"foo\", \"c\": \"foo bar\" }",
          },
          new Object[] {
              "nested replacement (string, reverse order)",
              // language=JSON
              "{ \"a\": \"${b} bar\", \"b\": \"foo\", \"c\": \"${a} dog\" }",
              // language=JSON
              "{ \"a\": \"foo bar\", \"b\": \"foo\", \"c\": \"foo bar dog\" }",
          },
          new Object[] {
              "nested replacement (string, ends)",
              // language=JSON
              "{ \"a\": \"dog ${c}\", \"b\": \"foo\", \"c\": \"bar ${b}\" }",
              // language=JSON
              "{ \"a\": \"dog bar foo\", \"b\": \"foo\", \"c\": \"bar foo\" }",
          },
          new Object[] {
              "nested replacement (string, ends, reverse order)",
              // language=JSON
              "{ \"a\": \"bar ${b}\", \"b\": \"foo\", \"c\": \"dog ${a}\" }",
              // language=JSON
              "{ \"a\": \"bar foo\", \"b\": \"foo\", \"c\": \"dog bar foo\" }",
          },
          new Object[] {
              "nested replacement (string, 2nd order)",
              // language=JSON
              "{ \"a\": \"bar ${b} ${c}\", \"b\": \"foo\", \"c\": \"dog ${b}\" }",
              // language=JSON
              "{ \"a\": \"bar foo dog foo\", \"b\": \"foo\", \"c\": \"dog foo\" }",
          },
          new Object[] {
              "nested replacement (string, 2nd order, reversed)",
              // language=JSON
              "{ \"a\": \"bar ${b}\", \"b\": \"foo\", \"c\": \"dog ${a} ${b}\" }",
              // language=JSON
              "{ \"a\": \"bar foo\", \"b\": \"foo\", \"c\": \"dog bar foo foo\" }",
          },
          new Object[] {
              "partially escaped replacement",
              // language=JSON
              "{ \"a\": \"bar $$${b}\", \"b\": \"foo\", \"c\": \"dog\" }",
              // language=JSON
              "{ \"a\": \"bar $foo\", \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "escaped replacement",
              // language=JSON
              "{ \"a\": \"bar $${b}\", \"b\": \"foo\", \"c\": \"dog\" }",
              // language=JSON
              "{ \"a\": \"bar ${b}\", \"b\": \"foo\", \"c\": \"dog\" }",
          },
          new Object[] {
              "escaped replacement (2nd order)",
              // language=JSON
              "{ \"a\": \"bar $${b}\", \"b\": \"foo\", \"c\": \"${a}\" }",
              // language=JSON
              "{ \"a\": \"bar ${b}\", \"b\": \"foo\", \"c\": \"bar ${b}\" }",
          },
          new Object[] {
              "escaped replacement (2nd order, reversed)",
              // language=JSON
              "{ \"a\": \"${c}\", \"b\": \"foo\", \"c\": \"dog $${b}\" }",
              // language=JSON
              "{ \"a\": \"dog ${b}\", \"b\": \"foo\", \"c\": \"dog ${b}\" }",
          },
      };
    }

    @Parameterized.Parameter
    public String desc;

    @Parameterized.Parameter(1)
    public String json;

    @Parameterized.Parameter(2)
    public String expected;

    @Test
    public void test() throws Exception
    {
      ObjectNode root = (ObjectNode) mapper.readTree(json);
      ObjectNode after = new VariableReplacementConfigurationLoader().load(root);
      assertEquals(mapper.readTree(expected), after);
    }
  }

  @RunWith(Parameterized.class)
  public static class load_shouldDetectCircularReferences
  {
    @Parameterized.Parameters(name = "{0}")
    public static Object[][] getParams()
    {
      return new Object[][]{
          // Note that this doesn't detect circular values, only the
          // references themselves. Circular values can be handled by Jackson.
          new Object[] {
              "one item: a->a",
              // language=JSON
              "{ \"a\": \"${a}\" }",
          },
          new Object[] {
              "two items: a->c->a",
              // language=JSON
              "{ \"a\": \"${c}\", \"b\": \"foo\", \"c\": \"${a}\" }",
          },
          new Object[] {
              "three items: a->b->c->a",
              // language=JSON
              "{ \"a\": \"${b}\", \"b\": \"${c}\", \"c\": \"${a}\" }",
          },
          new Object[] {
              // a is unnecessary here, technically
              "two-ish items str: a->b->c->b",
              // language=JSON
              "{ \"a\": \"x${b}\", \"b\": \"y${c}\", \"c\": \"z${b}\" }",
          },
      };
    }

    @Parameterized.Parameter
    public String desc;

    @Parameterized.Parameter(1)
    public String json;

    @Test(expected = CircularVariableReferenceException.class)
    public void test() throws Exception
    {
      ObjectNode root = (ObjectNode) mapper.readTree(json);
      new VariableReplacementConfigurationLoader().load(root);
    }
  }
}
