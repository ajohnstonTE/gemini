package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.techempower.helper.NetworkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;

public class ConfigurationSourceLoader
    implements ConfigurationLoader
{
  private final Logger                      log = LoggerFactory.getLogger(getClass());
  private final String                      configSources;
  private final ConfigurationSourcePlugin[] sourcePlugins;

  public ConfigurationSourceLoader()
  {
    this(getDefaultConfigSources(), getDefaultSourcePlugins());
  }

  public ConfigurationSourceLoader(ConfigurationSourcePlugin... sourcePlugins)
  {
    this(getDefaultConfigSources(), sourcePlugins);
  }

  public ConfigurationSourceLoader(String configSources,
                                   ConfigurationSourcePlugin... sourcePlugins)
  {
    this.configSources = configSources;
    this.sourcePlugins = sourcePlugins != null
        ? sourcePlugins : new ConfigurationSourcePlugin[0];
  }

  public ConfigurationSourceLoader(String configSources)
  {
    this(configSources, getDefaultSourcePlugins());
  }

  @Override
  public ObjectNode load(ObjectNode root) throws Exception
  {
    String[] sources = configSources.split(",");
    for (String source : sources)
    {
      root = isUrl(source)
          ? loadFromUrl(root, new UrlSource(source))
          : loadFromFileOrClassPath(root, new FileSource(source));
    }
    return root;
  }

  protected boolean isUrl(String source)
  {
    return NetworkHelper.isWebUrl(source);
  }

  protected ObjectNode loadFromUrl(ObjectNode node, UrlSource source)
      throws Exception
  {
    log.trace("Preparing to read from URL {}", source);
    ExtendsLoader extendsLoader = new ExtendsLoader(this);
    String contentType = source.getContentType();
    String fileExtension = source.getFileExtension();
    for (ConfigurationSourcePlugin sourcePlugin : sourcePlugins)
    {
      if (!sourcePlugin.supportsContentType(contentType)
          && !sourcePlugin.supportsFileExtension(fileExtension))
      {
        continue;
      }
      try (InputStream inputStream = source.read())
      {
        node = sourcePlugin.load(node, inputStream, extendsLoader);
      }
      log.trace("Successfully read from URL {}", source);
      return node;
    }
    throw new Exception("Content type/file extension not supported for "
        + "configuration source \"" + source + "\". Content type: "
        + contentType);
  }

  protected ObjectNode loadFromFileOrClassPath(ObjectNode node,
                                               FileSource source)
      throws Exception
  {
    log.trace("Preparing to read from file {}", source);
    ExtendsLoader extendsLoader = new ExtendsLoader(this);
    String fileExtension = source.getFileExtension();
    for (ConfigurationSourcePlugin sourcePlugin : sourcePlugins)
    {
      if (!sourcePlugin.supportsFileExtension(fileExtension))
      {
        continue;
      }
      InputStream inputStream = source.readFromSystemOrClassPath();
      if (inputStream != null)
      {
        try (inputStream)
        {
          node = sourcePlugin.load(node, inputStream, extendsLoader);
        }
        log.trace("Successfully read from file {}", source);
        return node;
      }
      throw new Exception("Could not read from configuration " +
          "source \"" + source + "\"");
    }
    throw new Exception("File extension not supported for configuration " +
        "source \"" + source + "\"");
  }

  /**
   * Omits the charset/etc, if present
   */
  private static String getContentTypeSimple(String source)
  {
    int i = source.indexOf(';');
    return i >= 0 ? source.substring(0, i) : source;
  }

  private static String getFileExtension(String source)
  {
    int i = source.lastIndexOf('.');
    return i >= 0 && i + 1 < source.length() ? source.substring(i + 1) : "";
  }

  private static String getDefaultConfigSources()
  {
    return Optional.ofNullable(System.getenv("CONFIG_SOURCES")).orElse("");
  }

  private static ConfigurationSourcePlugin[] getDefaultSourcePlugins()
  {
    return new ConfigurationSourcePlugin[]{
        new YamlSourcePlugin(),
        new JsonSourcePlugin(),
        new ConfSourcePlugin()
    };
  }
}
