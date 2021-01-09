package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConfiguratorV2
{
  private final ObjectMapper          mapper;
  private final ConfigurationLoader[] configurationLoaders;

  public ConfiguratorV2(ObjectMapper mapper,
                        ConfigurationLoader... configurationLoaders)
  {
    this.mapper = mapper;
    this.configurationLoaders = configurationLoaders != null
        ? configurationLoaders : new ConfigurationLoader[0];
  }

  public ConfiguratorV2(String configSources)
  {
    this(getDefaultObjectMapper(),
        new ConfigurationSourceLoader(configSources),
        new EnvironmentVariablesConfigurationLoader(),
        new SystemPropertiesConfigurationLoader(),
        new VariableReplacementConfigurationLoader(),
        new VariableCleanupConfigurationLoader());
  }

  public ConfiguratorV2()
  {
    this(getDefaultObjectMapper(), new ConfigurationSourceLoader(),
        new EnvironmentVariablesConfigurationLoader(),
        new SystemPropertiesConfigurationLoader(),
        new VariableReplacementConfigurationLoader(),
        new VariableCleanupConfigurationLoader());
  }

  public <T> T loadAs(Class<T> configClass) throws Exception
  {
    return mapper.readerFor(configClass).readValue(load());
  }

  protected ObjectNode load() throws Exception
  {
    ObjectNode root = new ObjectNode(JsonNodeFactory.instance);
    for (ConfigurationLoader configurationLoader : configurationLoaders)
    {
      root = configurationLoader.load(root);
    }
    return root;
  }

  protected static ObjectMapper getDefaultObjectMapper()
  {
    return new ObjectMapper();
  }
}
