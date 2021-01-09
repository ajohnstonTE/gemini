package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class PropertyAccessTest
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @RunWith(Parameterized.class)
  public static class fromPath
  {
    private static PropertyAccess.FieldAccess fieldAccess(PropertyAccess access,
                                                          String field)
    {
      return new PropertyAccess.FieldAccess(access, field);
    }

    private static PropertyAccess.ArrayAccess arrayAccess(PropertyAccess access,
                                                          int index)
    {
      return new PropertyAccess.ArrayAccess(access, index);
    }

    @Parameterized.Parameters(name = "{0}: {1} ")
    public static Object[][] getParams() throws JsonProcessingException
    {
      return new Object[][]{
          new Object[]{
              "field access",
              "a",
              fieldAccess(null, "a"),
          },
          new Object[]{
              "field access then field access",
              "a.b",
              fieldAccess(fieldAccess(null, "a"), "b"),
          },
          new Object[]{
              "field access then array access",
              "a[2]",
              arrayAccess(fieldAccess(null, "a"), 2),
          },
          new Object[]{
              "field access then array access then array access",
              "a[1][3]",
              arrayAccess(arrayAccess(fieldAccess(null, "a"), 1), 3)
          },
          new Object[]{
              "field access then array access then field access",
              "a[0].b",
              fieldAccess(arrayAccess(fieldAccess(null, "a"), 0), "b")
          },
          new Object[]{
              "field access then array access then field access then field access",
              "a[0].b.c",
              fieldAccess(fieldAccess(arrayAccess(fieldAccess(null, "a"), 0), "b"), "c")
          },
          new Object[]{
              "field access then field access then array access",
              "a.b[5]",
              arrayAccess(fieldAccess(fieldAccess(null, "a"), "b"), 5)
          },
          new Object[]{
              "field access then array access then field access then array access",
              "a[1].b[3]",
              arrayAccess(fieldAccess(arrayAccess(fieldAccess(null, "a"), 1), "b"), 3)
          },
      };
    }

    @Parameterized.Parameter
    public String desc;

    @Parameterized.Parameter(1)
    public String path;

    @Parameterized.Parameter(2)
    public PropertyAccess expected;

    @Test
    public void test() throws Exception
    {
      assertEquals(expected, PropertyAccess.fromPath(path));
    }
  }

  @RunWith(Parameterized.class)
  public static class read
  {
    @Parameterized.Parameters(name = "{0}: {2} ")
    public static Object[][] getParams() throws JsonProcessingException
    {
      return new Object[][]{
          new Object[]{
              "field access",
              // language=JSON
              "{ \"a\": \"dog\" }",
              "a",
              new TextNode("dog")
          },
          new Object[]{
              "field access then field access",
              // language=JSON
              "{ \"a\": { \"b\": true } }",
              "a.b",
              BooleanNode.TRUE
          },
          new Object[]{
              "array access",
              // language=JSON
              "{ \"a\": [ \"dog\" ] }",
              "a[0]",
              new TextNode("dog")
          },
          new Object[]{
              "array access then array access",
              // language=JSON
              "{ \"a\": [ [ \"dog\" ] ] }",
              "a[0][0]",
              new TextNode("dog")
          },
          new Object[]{
              "array access then field access",
              // language=JSON
              "{ \"a\": [ { \"b\": \"cow\" } ] }",
              "a[0].b",
              new TextNode("cow")
          },
          new Object[]{
              "non-zero array access",
              // language=JSON
              "{ \"a\": [ null, \"cat\" ] }",
              "a[1]",
              new TextNode("cat")
          },
          new Object[]{
              "non-zero array access then non-zero array access",
              // language=JSON
              "{ \"a\": [ null, [ null, null, null, \"bar\" ] ] }",
              "a[1][3]",
              new TextNode("bar")
          },
          new Object[]{
              "field access of array",
              // language=JSON
              "{ \"a\": [ \"dog\" ] }",
              "a",
              // language=JSON
              mapper.readTree("[ \"dog\" ]")
          },
          new Object[]{
              "nonexistent field access",
              // language=JSON
              "{ \"a\": \"dog\" }",
              "b",
              null
          },
          new Object[]{
              "null field access",
              // language=JSON
              "{ \"a\": null }",
              "a",
              mapper.nullNode()
          },
          new Object[]{
              "array access beyond array length",
              // language=JSON
              "{ \"a\": [ \"dog\" ] }",
              "a[1]",
              null
          },
      };
    }

    @Parameterized.Parameter
    public String desc;

    @Parameterized.Parameter(1)
    public String initial;

    @Parameterized.Parameter(2)
    public String path;

    @Parameterized.Parameter(3)
    public JsonNode expected;

    @Test
    public void test() throws Exception
    {
      ObjectNode root = (ObjectNode) mapper.readTree(initial);
      JsonNode result = PropertyAccess.fromPath(path).read(root);
      assertEquals(expected, result);
    }
  }

  @RunWith(Parameterized.class)
  public static class write
  {
    @Parameterized.Parameters(name = "{0}: {2} = {3} ")
    public static Object[][] getParams() throws JsonProcessingException
    {
      return new Object[][]{
          new Object[]{
              "field access",
              // language=JSON
              "{}",
              "a", "dog",
              // language=JSON
              "{ \"a\": \"dog\" }"
          },
          new Object[]{
              "field access then field access",
              // language=JSON
              "{}",
              "a.b", "cat",
              // language=JSON
              "{ \"a\": { \"b\": \"cat\" } }",
          },
          new Object[]{
              "array access",
              // language=JSON
              "{}",
              "a[0]", "cat",
              // language=JSON
              "{ \"a\": [ \"cat\" ] }",
          },
          new Object[]{
              "field access then array access",
              // language=JSON
              "{}",
              "a.b[0]", "cat",
              // language=JSON
              "{ \"a\": { \"b\": [\"cat\"] } }",
          },
          new Object[]{
              "array access then array access",
              // language=JSON
              "{}",
              "a[0][0]", "foo",
              // language=JSON
              "{ \"a\": [ [ \"foo\" ] ] }",
          },
          new Object[]{
              "array access then field access",
              // language=JSON
              "{}",
              "a[0].b", "bar",
              // language=JSON
              "{ \"a\": [ { \"b\": \"bar\" } ] }",
          },
          new Object[]{
              "array access at non-zero index in empty array",
              // language=JSON
              "{}",
              "a[1]", "foo",
              // language=JSON
              "{ \"a\": [ null, \"foo\" ] }",
          },
          new Object[]{
              "array access at larger index in empty array",
              // language=JSON
              "{}",
              "a[5]", "foo",
              // language=JSON
              "{ \"a\": [ null, null, null, null, null, \"foo\" ] }",
          },
          new Object[]{
              "array access then array access at non-zero index in empty array",
              // language=JSON
              "{}",
              "a[0][1]", "foo",
              // language=JSON
              "{ \"a\": [ [ null, \"foo\" ] ] }",
          },
          new Object[]{
              "array access then array access at larger index in empty array",
              // language=JSON
              "{}",
              "a[0][3]", "foo",
              // language=JSON
              "{ \"a\": [ [ null, null, null, \"foo\" ] ] }",
          },
          new Object[]{
              "field access with existing data",
              // language=JSON
              "{ \"b\": \"cat\" }",
              "a", "foo",
              // language=JSON
              "{ \"a\": \"foo\", \"b\": \"cat\" }",
          },
          new Object[]{
              "field access overwriting existing field",
              // language=JSON
              "{ \"a\": \"cat\" }",
              "a", "foo",
              // language=JSON
              "{ \"a\": \"foo\" }",
          },
          new Object[]{
              "field access clearing existing array",
              // language=JSON
              "{ \"a\": [ \"cat\" ] }",
              "a", "[]",
              // language=JSON
              "{ \"a\": [] }",
          },
          new Object[]{
              "array access appending to existing array",
              // language=JSON
              "{ \"a\": [ \"cat\" ] }",
              "a[1]", "foo",
              // language=JSON
              "{ \"a\": [ \"cat\", \"foo\" ] }",
          },
          new Object[]{
              "array access appending to existing array beyond current size",
              // language=JSON
              "{ \"a\": [ \"cat\" ] }",
              "a[4]", "foo",
              // language=JSON
              "{ \"a\": [ \"cat\", null, null, null, \"foo\" ] }",
          },
          new Object[]{
              "array access updating existing array element (1)",
              // language=JSON
              "{ \"a\": [ \"dog\", \"cat\" ] }",
              "a[0]", "foo",
              // language=JSON
              "{ \"a\": [ \"foo\", \"cat\" ] }",
          },
          new Object[]{
              "array access updating existing array element (2)",
              // language=JSON
              "{ \"a\": [ \"dog\", \"cat\" ] }",
              "a[1]", "foo",
              // language=JSON
              "{ \"a\": [ \"dog\", \"foo\" ] }",
          },
          new Object[]{
              "array access updating existing array element then field access",
              // language=JSON
              "{ \"a\": [ { \"b\": \"dog\" } ] }",
              "a[0].b", "foo",
              // language=JSON
              "{ \"a\": [ { \"b\": \"foo\" } ] }",
          },
          new Object[]{
              "field access to null value",
              // language=JSON
              "{ }",
              "a", "",
              // language=JSON
              "{ \"a\": null }",
          },
          new Object[]{
              "field access update to null value",
              // language=JSON
              "{ \"a\": \"dog\" }",
              "a", "",
              // language=JSON
              "{ \"a\": null }",
          },
      };
    }

    @Parameterized.Parameter
    public String desc;

    @Parameterized.Parameter(1)
    public String initial;

    @Parameterized.Parameter(2)
    public String path;

    @Parameterized.Parameter(3)
    public String value;

    @Parameterized.Parameter(4)
    public String expected;

    @Test
    public void test() throws Exception
    {
      ObjectNode root = (ObjectNode) mapper.readTree(initial);
      PropertyAccess.fromPath(path).write(root, value);
      assertEquals(mapper.readTree(expected), root);
    }
  }
}
