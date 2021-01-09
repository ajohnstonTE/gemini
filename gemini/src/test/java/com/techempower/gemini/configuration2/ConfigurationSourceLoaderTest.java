package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ConfigurationSourceLoaderTest
{
  @Test
  public void load() throws Exception
  {
    ObjectNode input = new ObjectNode(JsonNodeFactory.instance);
    ObjectNode jsonOutput = new ObjectNode(JsonNodeFactory.instance);
    ObjectNode yamlOutput = new ObjectNode(JsonNodeFactory.instance);
    TestPlugin jsonPlugin = new TestPlugin("application/json", "json",
        jsonOutput);
    TestPlugin yamlPlugin = new TestPlugin(null, "yml", yamlOutput);
    assertSame(jsonOutput, new ConfigurationSourceLoader(
        "https://jsonplaceholder.typicode.com/todos/1", jsonPlugin, yamlPlugin)
        .load(input));
    assertSame(jsonOutput, new ConfigurationSourceLoader(
        "http://date.jsontest.com/", jsonPlugin, yamlPlugin)
        .load(input));
    assertSame(yamlOutput, new ConfigurationSourceLoader(
        "config-test/sample-from-classpath.yml", jsonPlugin, yamlPlugin)
        .load(input));
    Path tempFile = null;
    try
    {
      tempFile = Files.createTempFile("sample-from-system", ".yml");
      assertSame(yamlOutput, new ConfigurationSourceLoader(
          tempFile.toString(), jsonPlugin, yamlPlugin)
          .load(input));
    }
    finally
    {
      if (tempFile != null)
      {
        // Clean up temp file, not that it's necessary.
        Files.deleteIfExists(tempFile);
      }
    }
  }

  private static class TestPlugin
      implements ConfigurationSourcePlugin
  {
    private final String     contentType;
    private final String     fileExtension;
    private final ObjectNode output;

    private TestPlugin(String contentType,
                       String fileExtension,
                       ObjectNode output)
    {
      this.contentType = contentType;
      this.fileExtension = fileExtension;
      this.output = output;
    }

    @Override
    public boolean supportsContentType(String contentType)
    {
      return this.contentType != null
          && this.contentType.equalsIgnoreCase(contentType);
    }

    @Override
    public boolean supportsFileExtension(String fileExtension)
    {
      return this.fileExtension != null
          && this.fileExtension.equalsIgnoreCase(fileExtension);
    }

    @Override
    public ObjectNode load(ObjectNode root,
                           InputStream inputStream,
                           ExtendsLoader extendsLoader) throws Exception
    {
      return output;
    }
  }
}