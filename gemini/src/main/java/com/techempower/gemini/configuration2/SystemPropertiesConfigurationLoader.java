package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class SystemPropertiesConfigurationLoader
    implements ConfigurationLoader
{
  private static final String CONFIG_PREFIX        = "config.";
  private static final int    CONFIG_PREFIX_LENGTH = CONFIG_PREFIX.length();

  @Override
  public ObjectNode load(ObjectNode root) throws Exception
  {
    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
    {
      if (entry.getKey() instanceof String
          && entry.getValue() instanceof String)
      {
        String key = (String) entry.getKey();
        if (key.startsWith(CONFIG_PREFIX))
        {
          key = key.substring(CONFIG_PREFIX_LENGTH);
          PropertyAccess.fromPath(key).write(root, (String)entry.getValue());
        }
      }
    }
    return root;
  }
}
