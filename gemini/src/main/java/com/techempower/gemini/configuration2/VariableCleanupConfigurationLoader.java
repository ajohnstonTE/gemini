package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class VariableCleanupConfigurationLoader
  implements ConfigurationLoader
{
  @Override
  public ObjectNode load(ObjectNode root) throws Exception
  {
    root.remove("var");
    root.remove("extends");
    return root;
  }
}
