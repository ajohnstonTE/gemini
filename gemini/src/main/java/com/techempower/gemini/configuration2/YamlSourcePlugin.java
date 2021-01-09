package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;

public class YamlSourcePlugin
    implements ConfigurationSourcePlugin
{
  private final ObjectMapper mapper;

  public YamlSourcePlugin(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public YamlSourcePlugin()
  {
    this(getDefaultObjectMapper());
  }

  @Override
  public boolean supportsContentType(String contentType)
  {
    return false;
  }

  @Override
  public boolean supportsFileExtension(String fileExtension)
  {
    return "yml".equalsIgnoreCase(fileExtension)
        || "yaml".equalsIgnoreCase(fileExtension);
  }

  @Override
  public ObjectNode load(ObjectNode root,
                         InputStream inputStream,
                         ExtendsLoader extendsLoader) throws Exception
  {
    JsonNode jsonNode = mapper.readTree(inputStream);
    if (!jsonNode.isObject())
    {
      throw new Exception("YAML configuration files must be an object.");
    }
    root.setAll((ObjectNode) jsonNode);
    return root;
  }

  protected static ObjectMapper getDefaultObjectMapper()
  {
    return new ObjectMapper(new YAMLFactory());
  }
}
