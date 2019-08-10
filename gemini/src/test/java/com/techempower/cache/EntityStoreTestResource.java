package com.techempower.cache;

import com.techempower.TechEmpowerApplication;
import com.techempower.Version;
import com.techempower.data.EntityGroup;
import com.techempower.data.jdbc.BasicConnectorFactory;
import com.techempower.log.ComponentLog;
import com.techempower.log.Log;
import com.techempower.log.LogListener;
import com.techempower.util.EnhancedProperties;
import com.techempower.util.Identifiable;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

/**
 * May be used whenever the entity store's full functionality is required.
 * Spins up a MySQL db instance in docker used for all tests. Before each test
 * is run, the given migration script (if any) is run, and any entity groups
 * are registered. After each test the database is completely wiped. Note that
 * the database migration scripts are run per-directory. If you wish to add a
 * new migration script for a new test, generally put it in its own directory.
 */
@Testcontainers
public class EntityStoreTestResource
    implements BeforeTestExecutionCallback, AfterTestExecutionCallback
{
  @Container
  public static MySQLContainer container = new MySQLContainer();

  static
  {
    container.start();
    flyway().load().baseline();
  }

  public static class MockCacheResourceBuilder
  {
    private List<EntityGroup.Builder<? extends Identifiable>> entityGroupBuilders = new ArrayList<>();
    private String setUpSqlScript = null;

    public MockCacheResourceBuilder register(
        EntityGroup.Builder<? extends Identifiable> entityGroupBuilder)
    {
      this.entityGroupBuilders.add(entityGroupBuilder);
      return this;
    }

    public MockCacheResourceBuilder setSqlScripts(String setUpSqlScript)
    {
      this.setUpSqlScript = setUpSqlScript;
      return this;
    }

    public EntityStoreTestResource build()
    {
      return new EntityStoreTestResource(this);
    }
  }

  public static MockCacheResourceBuilder builder()
  {
    return new MockCacheResourceBuilder();
  }

  private final List<EntityGroup.Builder<? extends Identifiable>> entityGroupBuilders;
  private final String setUpSqlScript;
  private final TechEmpowerApplication application;
  private final BasicConnectorFactory factory;
  public EntityStore store;

  private EntityStoreTestResource(MockCacheResourceBuilder builder)
  {
    Log log = new Log()
    {

      @Override
      public void log(String componentCode, String logString, int debugLevel)
      {
      }

      @Override
      public void log(String componentCode, String logString)
      {
      }

      @Override
      public void log(String logString, int debugLevel)
      {
      }

      @Override
      public void log(String logString)
      {
      }

      @Override
      public ComponentLog getComponentLog(String componentCode)
      {
        return new ComponentLog(this, componentCode);
      }

      @Override
      public void assertion(String componentCode, boolean eval, String debugString, int debugLevel)
      {
      }

      @Override
      public void assertion(String componentCode, boolean eval, String logString)
      {
      }

      @Override
      public void assertion(boolean eval, String logString, int debugLevel)
      {
      }

      @Override
      public void assertion(boolean eval, String logString)
      {
      }

      @Override
      public void configure(EnhancedProperties props, Version version)
      {
      }

      @Override
      public List<LogListener> getLogListeners()
      {
        return new ArrayList<>();
      }

      @Override
      public void addListener(LogListener listener)
      {
      }

      @Override
      public boolean removeListener(LogListener listener)
      {
        return false;
      }
    };
    application = new TechEmpowerApplication()
    {
      @Override
      public ComponentLog getLog(String componentCode)
      {
        return new ComponentLog(log, componentCode);
      }
    };
    factory = new BasicConnectorFactory(application, "DB.");
    EnhancedProperties props = new EnhancedProperties();
    props.put("DB.ConnectString", container.getJdbcUrl().substring(1) + "?useSSL=false" );
    props.put("DB.LoginName", container.getUsername());
    props.put("DB.LoginPass", container.getPassword());
    props.put("DB.Driver.Class", container.getDriverClassName());
    props.put("DB.Driver.UrlPrefix", container.getJdbcUrl().substring(0, 1));
    factory.configure(props);
    store = new EntityStore(application, factory);
    entityGroupBuilders = builder.entityGroupBuilders;
    setUpSqlScript = builder.setUpSqlScript;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception
  {
    if (setUpSqlScript != null)
    {
      flyway().locations("classpath:" + setUpSqlScript).load().migrate();
    }
    for (EntityGroup.Builder<? extends Identifiable> entityGroupBuilder : entityGroupBuilders)
    {
      EntityGroup<? extends Identifiable> entityGroup = entityGroupBuilder.build(store);
      store.register(entityGroup);
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception
  {
    flyway().locations(new Location[0]).load().clean();
    store = new EntityStore(application, factory);
  }

  private static FluentConfiguration flyway()
  {
    return Flyway.configure(ClassLoader.getSystemClassLoader())
        .dataSource(
            container.getJdbcUrl()  + "?useSSL=false",
            container.getUsername(),
            container.getPassword());
  }
}
