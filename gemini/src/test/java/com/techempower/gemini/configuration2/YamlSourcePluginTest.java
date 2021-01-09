package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class YamlSourcePluginTest
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @Parameterized.Parameters(name = "{0}")
  public static Object[][] getParams()
  {
    return new Object[][]{
        new Object[]{
            "config-test/config.yml",
            // language=JSON
            "{ " +
                "\"description\": \"App B\", " +
                "\"threads\": 4 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config.yml",
                "config-test/config-threads.yml",
            }),
            // language=JSON
            "{ " +
                "\"description\": \"App B\", " +
                "\"threads\": 8 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config-threads.yml",
                "config-test/config.yml",
            }),
            // language=JSON
            "{ " +
                "\"description\": \"App B\", " +
                "\"threads\": 4 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/merge-test.yml",
            }),
            // language=JSON
            "{ " +
                "\"description\": \"App D\", " +
                "\"threads\": 8 " +
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
      FileOrClassPathReader sourceLoader = new FileOrClassPathReader()
      {
        @Override
        public ObjectNode loadFromFileOrClassPath(ObjectNode to,
                                                  FileSource from) throws Exception
        {
          return new YamlSourcePlugin().load(to, from
              .readFromSystemOrClassPath(), new ExtendsLoader(this));
        }
      };
      root = new YamlSourcePlugin().load(root, new FileSource(source)
          .readFromSystemOrClassPath(), new ExtendsLoader(sourceLoader));
    }
    assertEquals(mapper.readTree(expected), root);
  }
}
