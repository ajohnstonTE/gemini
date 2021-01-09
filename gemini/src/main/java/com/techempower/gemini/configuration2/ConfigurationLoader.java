package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ConfigurationLoader
{
  ObjectNode load(ObjectNode root) throws Exception;
}
