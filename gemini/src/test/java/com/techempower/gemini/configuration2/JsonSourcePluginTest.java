package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class JsonSourcePluginTest
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @Parameterized.Parameters(name = "{0}")
  public static Object[][] getParams()
  {
    return new Object[][]{
        new Object[]{
            "config-test/config.json",
            // language=JSON
            "{ " +
                "\"description\": \"App A\", " +
                "\"threads\": 1 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config.json",
                "config-test/config-description.json",
            }),
            // language=JSON
            "{ " +
                "\"description\": \"App C\", " +
                "\"threads\": 1 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config-description.json",
                "config-test/config.json",
            }),
            // language=JSON
            "{ " +
                "\"description\": \"App A\", " +
                "\"threads\": 1 " +
                "}",
        },
    };
  }

  @Parameterized.Parameter
  public String sources;

  @Parameterized.Parameter(1)
  public String expected;

  @Test
  public void load() throws Exception
  {
    ObjectNode root = mapper.createObjectNode();
    for (String source : sources.split(","))
    {
      root = new JsonSourcePlugin().load(root, new FileSource(source)
          .readFromSystemOrClassPath(), null);
    }
    assertEquals(mapper.readTree(expected), root);
  }
}
