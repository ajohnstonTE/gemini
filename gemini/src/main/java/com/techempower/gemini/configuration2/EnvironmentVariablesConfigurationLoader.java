package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class EnvironmentVariablesConfigurationLoader
    implements ConfigurationLoader
{
  @Override
  public ObjectNode load(ObjectNode root) throws Exception
  {
    for (String key : System.getenv().keySet())
    {
      var varNode = root.get("var");
      if (varNode == null)
      {
        varNode = new ObjectNode(JsonNodeFactory.instance);
        root.set("var", varNode);
      }
      var envNode = varNode.get("env");
      if (envNode == null)
      {
        envNode = new ObjectNode(JsonNodeFactory.instance);
        ((ObjectNode) varNode).set("env", envNode);
      }
      JsonNode node;
      String envValue = System.getenv(key);
      try {
        // Best-effort try to read the value like YAML, but if the value is
        // invalid YAML then it can just be text.
        node = new YamlValueParser().getValueFromString(envValue);
      } catch (Exception e) {
        node = new TextNode(envValue);
      }
      ((ObjectNode) envNode).set(key, node);
    }
    return root;
  }
}
