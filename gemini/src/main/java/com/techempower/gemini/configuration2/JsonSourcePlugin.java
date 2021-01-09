package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.InputStream;

public class JsonSourcePlugin
    implements ConfigurationSourcePlugin
{
  private final ObjectMapper mapper;

  public JsonSourcePlugin(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public JsonSourcePlugin()
  {
    this(getDefaultObjectMapper());
  }

  @Override
  public boolean supportsContentType(String contentType)
  {
    return "application/json".equalsIgnoreCase(contentType);
  }

  @Override
  public boolean supportsFileExtension(String fileExtension)
  {
    return "json".equalsIgnoreCase(fileExtension);
  }

  @Override
  public ObjectNode load(ObjectNode root,
                         InputStream inputStream,
                         ExtendsLoader extendsLoader) throws Exception
  {
    JsonNode jsonNode = mapper.readTree(inputStream);
    if (!jsonNode.isObject())
    {
      throw new Exception("JSON configuration files must be an object.");
    }
    root.setAll((ObjectNode) jsonNode);
    return root;
  }

  protected static ObjectMapper getDefaultObjectMapper()
  {
    return new ObjectMapper();
  }
}
