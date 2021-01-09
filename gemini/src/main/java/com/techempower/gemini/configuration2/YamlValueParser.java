package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlValueParser
{
  private static final ObjectReader READER = new ObjectMapper(
      new YAMLFactory()).readerFor(SingleValue.class);

  // This is incredibly dumb. Just saying.
  public JsonNode getValueFromString(String str)
      throws JsonProcessingException
  {
    return READER.readTree("value: " + str)
      .get("value");
  }

  public static class SingleValue
  {
    private Object value;

    public Object getValue()
    {
      return value;
    }

    public void setValue(Object value)
    {
      this.value = value;
    }
  }
}
