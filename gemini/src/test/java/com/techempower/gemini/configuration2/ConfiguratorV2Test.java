package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ConfiguratorV2Test
{
  private static final ObjectMapper mapper = new ObjectMapper();

  @Parameterized.Parameters(name = "{0}")
  public static Object[][] getParams()
  {
    return new Object[][]{
        new Object[]{
            "config-test/config.json",
            AppConfig.class,
            new AppConfig("App A", 1),
            // language=JSON
            "{ " +
                "\"description\": \"App A\", " +
                "\"threads\": 1 " +
                "}",
        },
        new Object[]{
            "config-test/config.yml",
            AppConfig.class,
            new AppConfig("App B", 4),
            // language=JSON
            "{ " +
                "\"description\": \"App B\", " +
                "\"threads\": 4 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config.json",
                "config-test/config-threads.yml",
            }),
            AppConfig.class,
            new AppConfig("App A", 8),
            // language=JSON
            "{ " +
                "\"description\": \"App A\", " +
                "\"threads\": 8 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config-threads.yml",
                "config-test/config.json",
            }),
            AppConfig.class,
            new AppConfig("App A", 1),
            // language=JSON
            "{ " +
                "\"description\": \"App A\", " +
                "\"threads\": 1 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config.yml",
                "config-test/config-description.json",
            }),
            AppConfig.class,
            new AppConfig("App C", 4),
            // language=JSON
            "{ " +
                "\"description\": \"App C\", " +
                "\"threads\": 4 " +
                "}",
        },
        new Object[]{
            String.join(",", new String[]{
                "config-test/config-description.json",
                "config-test/config.yml",
            }),
            AppConfig.class,
            new AppConfig("App B", 4),
            // language=JSON
            "{ " +
                "\"description\": \"App B\", " +
                "\"threads\": 4 " +
                "}",
        },
    };
  }

  @Parameterized.Parameter
  public String sources;

  @Parameterized.Parameter(1)
  public Class<?> type;

  @Parameterized.Parameter(2)
  public Object expectedInstance;

  @Parameterized.Parameter(3)
  public String expectedJson;

  @Test
  public void loadAs() throws Exception
  {
    assertEquals(expectedInstance, new ConfiguratorV2(sources).loadAs(type));
  }

  @Test
  public void load() throws Exception
  {
    assertEquals(mapper.readTree(expectedJson), new ConfiguratorV2(sources).load());
  }

  public static class Foo
  {
    private List<Object> blah;

    public Foo()
    {
    }

    public Foo(List<Object> blah)
    {
      this.blah = blah;
    }

    public List<Object> getBlah()
    {
      return blah;
    }

    public void setBlah(List<Object> blah)
    {
      this.blah = blah;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (!(o instanceof Foo)) return false;
      Foo foo = (Foo) o;
      return Objects.equals(getBlah(), foo.getBlah());
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(getBlah());
    }

    @Override
    public String toString()
    {
      return new StringJoiner(", ", Foo.class.getSimpleName() + "[", "]")
          .add("blah=" + blah)
          .toString();
    }
  }

  public static class AppConfig
  {
    private int    threads;
    private String description;

    public AppConfig()
    {
      // No-param constructor required for instantiation by Jackson
    }

    public AppConfig(String description, int threads)
    {
      this.threads = threads;
      this.description = description;
    }

    public int getThreads()
    {
      return threads;
    }

    public void setThreads(int threads)
    {
      this.threads = threads;
    }

    public String getDescription()
    {
      return description;
    }

    public void setDescription(String description)
    {
      this.description = description;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (!(o instanceof AppConfig)) return false;
      AppConfig appConfig = (AppConfig) o;
      return Objects.equals(getDescription(), appConfig.getDescription())
          && getThreads() == appConfig.getThreads();
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(getDescription(), getThreads());
    }

    @Override
    public String toString()
    {
      return new StringJoiner(", ", AppConfig.class.getSimpleName() + "[", "]")
          .add("description='" + description + "'")
          .add("threads=" + threads)
          .toString();
    }
  }
}
