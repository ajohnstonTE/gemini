package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ConfSourcePluginTest
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @Parameterized.Parameters(name = "{0}")
  public static Object[][] getParams()
  {
    return new Object[][]{
        new Object[]{
            "config-test/config-large.conf",
            // language=JSON
            "{" +
                "  \"example\": \"test\"," +
                "  \"example2\": \"test more\"," +
                "  \"root_empty_array\": []," +
                "  \"root_null_array\": null," +
                "  \"root_populated_array\": [" +
                "    4," +
                "    true," +
                "    \"example\"," +
                "    null," +
                "    \"another example\"" +
                "  ]," +
                "  \"array_of_objects\": [" +
                "    {" +
                "      \"propA\": \"something\"," +
                "      \"propB\": false" +
                "    }," +
                "    {" +
                "      \"propA\": \"that\"," +
                "      \"propB\": true" +
                "    }" +
                "  ]," +
                "  \"person\": {" +
                "    \"name\": \"John\"," +
                "    \"age\": \"${environment.age}\"," +
                "    \"phone\": \"${example2}\"," +
                "    \"lastName\": \"${person.phone}\"," +
                "    \"self\": \"${person.self}\"" +
                "  }," +
                "  \"recursive\": \"${resursive}\"," +
                "  \"escaped\": \"$${example}\"," +
                "  \"populated_quoted_string\": \"true\"," +
                "  \"escaped_populated_quoted_string\": \"'true'\"," +
                "  \"populated_double_quoted_string\": \"true\"," +
                "  \"escaped_populated_double_quoted_string\": \"\\\"true\\\"\"," +
                "  \"populated_double_then_single_quoted_string\": \"\\\"true'\"," +
                "  \"populated_single_then_double_quoted_string\": \"'true\\\"\"," +
                "  \"empty_string\": \"\"," +
                "  \"empty_double_quote_string\": \"\"," +
                "  \"null_string\": null," +
                "  \"complicated\": [" +
                "    [" +
                "      null," +
                "      {" +
                "        \"object\": {" +
                "          \"that\": [" +
                "            {" +
                "              \"does\": {" +
                "                \"a\": {" +
                "                  \"lot\": [" +
                "                    null," +
                "                    null," +
                "                    {" +
                "                      \"of\": [" +
                "                        null," +
                "                        null," +
                "                        null," +
                "                        {" +
                "                          \"stuff\": \"foo\"," +
                "                          \"more\": {" +
                "                            \"stuff\": true" +
                "                          }," +
                "                          \"other\": {" +
                "                            \"stuff\": {" +
                "                              \"too\": []" +
                "                            }" +
                "                          }" +
                "                        }" +
                "                      ]" +
                "                    }" +
                "                  ]" +
                "                }" +
                "              }" +
                "            }" +
                "          ]" +
                "        }" +
                "      }" +
                "    ]" +
                "  ]" +
                "}"
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
          return new ConfSourcePlugin().load(to, from
              .readFromSystemOrClassPath(), new ExtendsLoader(this));
        }
      };
      root = new ConfSourcePlugin().load(root, new FileSource(source)
          .readFromSystemOrClassPath(), new ExtendsLoader(sourceLoader));
    }
    assertEquals(mapper.readTree(expected), root);
  }
}
