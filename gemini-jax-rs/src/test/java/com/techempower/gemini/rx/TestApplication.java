package com.techempower.gemini.rx;

import com.techempower.data.ConnectorFactory;
import com.techempower.data.jdbc.BasicConnectorFactory;
import com.techempower.gemini.*;
import com.techempower.gemini.params.Parameters;
import com.techempower.gemini.path.PathDispatcher;
import com.techempower.util.EnhancedProperties;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.*;
import org.testcontainers.containers.MySQLContainer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TestApplication
    extends UndertowGeminiApplication
{

  SynchronousDispatcher restEasyDispatcher;

  @Override
  protected ConnectorFactory constructConnectorFactory()
  {
    return new BasicConnectorFactory(this, "db.");
  }

  @Override
  protected Lifecycle constructLifecycle()
  {
    return new Lifecycle(this)
    {
      @Override
      protected void initializationTasksComplete()
      {
        super.initializationTasksComplete();
        TestApplication.this.initComplete();
      }
    };
  }

  public List<Runnable> runnables;

  public void addInitCompleteTask(Runnable runnable)
  {
    if (runnables == null)
    {
      runnables = new ArrayList<>();
    }
    if (this.isRunning())
    {
      runnable.run();
    }
    else
    {
      runnables.add(runnable);
    }
  }

  public void initComplete()
  {
    if (this.runnables != null)
    {
      this.runnables.forEach(Runnable::run);
    }
  }

  @Override
  protected Configurator constructConfigurator()
  {
    return new Configurator(this)
    {
      @Override
      protected EnhancedProperties getConfigurationFromProviders()
      {
        EnhancedProperties props = super.getConfigurationFromProviders();
        MySQLContainer container = FullStackDispatcherIntegrationTest.container;
        props.put("db.Driver.UrlPrefix", "jdbc:mysql://");
        props.put("db.Driver.Class", container.getDriverClassName());
        props.put("db.ConnectString", container.getJdbcUrl().substring("jdbc:mysql://".length()));
        props.put("db.LoginName", container.getUsername());
        props.put("db.LoginPass", container.getPassword());
        return props;
      }
    };
  }

  @Override
  protected Dispatcher constructDispatcher()
  {
    PathDispatcher.Configuration<Context> config = new PathDispatcher.Configuration<>();
    config
        .add("foo", new StandardGeminiHandler(this))
        .add(new LoggingExceptionHandler(this));
    return new PathDispatcher<TestApplication, Context>(this, config)
    {
      {
        ResteasyProviderFactory providerFactory = ResteasyProviderFactory.newInstance();
        restEasyDispatcher = new SynchronousDispatcher(providerFactory);
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplication(new JaxRsApplication());
        deployment.setDispatcher(restEasyDispatcher);
        deployment.setProviderFactory(providerFactory);
        providerFactory.setRegisterBuiltins(true);
        RegisterBuiltin.register(providerFactory);
        providerFactory.registerProvider(JacksonJsonProvider.class);
        providerFactory.setBuiltinsRegistered(true);
        deployment.setRegistry(restEasyDispatcher.getRegistry());
        deployment.registration();
      }

      @Override
      public boolean dispatch(Context context)
      {
        Parameters parameters = new Parameters();
        Collections.list(context.getRequest().getHeaderNames())
            .forEach(name ->
                parameters.putSingle(name, context.headers().get(name)));
        AtomicInteger statusCode = new AtomicInteger(200);
        MultivaluedTreeMap<String, Object> outputHeaders = new MultivaluedTreeMap<>();
        AtomicBoolean flushed = new AtomicBoolean(false);
        HttpResponse response = new HttpResponse()
        {
          @Override
          public int getStatus()
          {
            return statusCode.get();
          }

          @Override
          public void setStatus(int status)
          {
            statusCode.set(status);
            context.setStatus(status);
          }

          @Override
          public MultivaluedMap<String, Object> getOutputHeaders()
          {
            return outputHeaders;
          }

          @Override
          public OutputStream getOutputStream() throws IOException
          {
            return context.getOutputStream();
          }

          @Override
          public void setOutputStream(OutputStream os)
          {
            // I don't even care
          }

          @Override
          public void addNewCookie(NewCookie cookie)
          {

          }

          @Override
          public void sendError(int status) throws IOException
          {

          }

          @Override
          public void sendError(int status, String message) throws IOException
          {

          }

          @Override
          public boolean isCommitted()
          {
            return false;
          }

          @Override
          public void reset()
          {

          }

          @Override
          public void flushBuffer() throws IOException
          {
            // Probably wrong. Idc.
            context.getOutputStream().flush();
            flushed.set(true);
          }
        };
        AtomicReference<InputStream> inputStreamAtomicReference = new AtomicReference<>();
        AtomicReference<String> requestMethodAtomicReference = new AtomicReference<>();
        HttpHeaders headers = new ResteasyHttpHeaders(parameters);
        HttpRequest request = new BaseHttpRequest(new ResteasyUriInfo(
            context.getRequestUri(),
            context.getQueryString(),
            ""))
        {

          @Override
          public HttpHeaders getHttpHeaders()
          {
            return headers;
          }

          @Override
          public MultivaluedMap<String, String> getMutableHeaders()
          {
            return parameters;
          }

          @Override
          public InputStream getInputStream()
          {
            if (inputStreamAtomicReference.get() != null)
            {
              return inputStreamAtomicReference.get();
            }
            return ((UndertowHttpRequest)context.getRequest()).getInputStream();
          }

          @Override
          public void setInputStream(InputStream stream)
          {
            inputStreamAtomicReference.set(stream);
          }

          @Override
          public String getHttpMethod()
          {
            if (requestMethodAtomicReference.get() != null)
            {
              return requestMethodAtomicReference.get();
            }
            return context.getRequestMethod().name();
          }

          @Override
          public void setHttpMethod(String method)
          {
            requestMethodAtomicReference.set(method);
          }

          @Override
          public Object getAttribute(String attribute)
          {
            return context.getRequest().getAttribute(attribute);
          }

          @Override
          public void setAttribute(String name, Object value)
          {
            context.getRequest().setAttribute(name, value);
          }

          @Override
          public void removeAttribute(String name)
          {
            // This probably won't work. idc.
            context.getRequest().setAttribute(name, null);
          }

          @Override
          public Enumeration<String> getAttributeNames()
          {
            return ((UndertowHttpRequest)context.getRequest()).getAttributeNames();
          }

          @Override
          public ResteasyAsynchronousContext getAsyncContext()
          {
            return new SynchronousExecutionContext(restEasyDispatcher, this, response);
          }

          @Override
          public void forward(String path)
          {

          }

          @Override
          public boolean wasForwarded()
          {
            return false;
          }
        };
        restEasyDispatcher.invoke(request, response);
        return flushed.get() || super.dispatch(context);
      }
    };
  }

}
