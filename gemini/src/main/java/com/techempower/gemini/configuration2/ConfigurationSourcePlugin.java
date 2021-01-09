package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.InputStream;

public interface ConfigurationSourcePlugin
{
  boolean supportsContentType(String contentType);

  boolean supportsFileExtension(String fileExtension);

  ObjectNode load(ObjectNode root,
                  InputStream inputStream,
                  ExtendsLoader extendsLoader) throws Exception;
}
