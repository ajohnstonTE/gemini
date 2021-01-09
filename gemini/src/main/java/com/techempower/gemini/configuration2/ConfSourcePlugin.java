package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfSourcePlugin
    implements ConfigurationSourcePlugin
{
  @Override
  public boolean supportsContentType(String contentType)
  {
    return false;
  }

  @Override
  public boolean supportsFileExtension(String fileExtension)
  {
    return "conf".equalsIgnoreCase(fileExtension);
  }

  @Override
  public ObjectNode load(ObjectNode root,
                         InputStream inputStream,
                         ExtendsLoader extendsLoader) throws Exception
  {
    Properties properties = new Properties();
    properties.load(inputStream);
    ObjectNode base = new ObjectNode(JsonNodeFactory.instance);
    if (properties.contains("extends"))
    {
      extendsLoader.loadExtends(base, properties.getProperty("extends"));
      properties.remove("extends");
    }
    Map<Object, Object> orderedProperties = properties.entrySet()
        .stream()
        .filter(entry -> entry.getKey() instanceof String
            && entry.getValue() instanceof String)
        .sorted(Comparator.comparing(entry -> (String) entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            // Conflicts won't occur
            (a, b) -> a,
            // Create a LinkedHashMap to maintain sort order
            LinkedHashMap::new));
    for (Map.Entry<Object, Object> entry : orderedProperties.entrySet())
    {
      if (entry.getKey() instanceof String
          && entry.getValue() instanceof String)
      {
        String key = (String) entry.getKey();
        String value = (String) entry.getValue();
        PropertyAccess.fromPath(key).write(base, value);
      }
    }
    root.setAll(base);
    return root;
  }
}
