package com.techempower.gemini.rx;

import com.techempower.gemini.*;
import com.techempower.gemini.context.Attachments;
import com.techempower.gemini.feature.FeatureManager;
import com.techempower.gemini.path.MethodSegmentHandler;
import com.techempower.gemini.path.PathDispatcher;
import com.techempower.gemini.path.PathSegments;
import com.techempower.gemini.path.RequestReferences;
import com.techempower.gemini.path.annotation.Get;
import com.techempower.gemini.path.annotation.PathSegment;
import com.techempower.gemini.pyxis.BasicUser;
import com.techempower.gemini.simulation.GetSimRequest;
import com.techempower.gemini.simulation.SimClient;
import com.techempower.gemini.simulation.SimSessionManager;
import com.techempower.log.ComponentLog;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class StandardDispatchTest
{
  @Test
  void dispatchTestTypical()
  {
    // This is one of several painful approaches to testing an endpoint.
    // You can do it more directly, but it's painful no matter what.
    // This code may look "clean-ish", but ALL the code below the third test
    // was needed to make these last two tests possible. The code needed to
    // make the test above possible is just the code above. The feature isn't
    // even *implemented* yet, but that test is passing all the same. But
    // that's the idea.
    GeminiApplication application = mockedApp();

    PathDispatcher<GeminiApplication, Context> pathDispatcher =
        new PathDispatcher<>(application, new PathDispatcher.Configuration<>()
            .add("example", new JaxRsDispatcherTest.ExampleHandler(application))
            .add(new LoggingExceptionHandler(application)));
    Simulation simulation = new JaxRsDispatcherTest.StandardSimulation()
    {
      @Override
      public GeminiApplication getApplication()
      {
        return application;
      }
    };
    {
      JaxRsDispatcherTest.SpyAwareContext context = spy(new JaxRsDispatcherTest.SpyAwareContext(application,
          new JaxRsDispatcherTest.AnotherGetSimRequest(
              simulation,
              "example/test",
              new HashMap<>(),
              new SimClient(1),
              application)));
      context.setUp(context);
      ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
      pathDispatcher.dispatch(context);
      verify(context).print(argument.capture());
      assertEquals("Hello, World!", argument.getValue());
    }
    {
      JaxRsDispatcherTest.SpyAwareContext context = spy(new JaxRsDispatcherTest.SpyAwareContext(application,
          new JaxRsDispatcherTest.AnotherGetSimRequest(
              simulation,
              "example/test2",
              new HashMap<String, String>()
              {{
                put("num", "5");
              }},
              new SimClient(1),
              application)));
      context.setUp(context);
      ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
      pathDispatcher.dispatch(context);
      verify(context).print(argument.capture());
      assertEquals("Hello, World! num: 5", argument.getValue());
    }
  }

  public static class SpyAwareContext
      extends Context
  {
    public SpyAwareContext(GeminiApplication application, Request request)
    {
      super(application, request);
    }

    @Override
    public Attachments files()
    {
      return null;
    }

    public void setUp(Context context)
    {
      RequestReferences.set(context,
          new PathSegments(context.getRequestUri()));
      CONTEXTS_BY_THREAD.set(context);
    }
  }

  public static abstract class StandardSimulation
      extends Simulation
  {
    // Can't simply have application as a property because
    // it's used in the super before it can be set here.

    @Override
    protected String getDocroot()
    {
      return null;
    }

    @Override
    protected Class<? extends BasicUser> getUserClass()
    {
      return null;
    }
  }

  public static class AnotherGetSimRequest
      extends GetSimRequest
  {
    public AnotherGetSimRequest(Simulation simulation,
                                String url,
                                Map<String, String> parameters,
                                SimClient client,
                                GeminiApplication application)
    {
      super(simulation, url, parameters, client, application);
    }

    @Override
    public Request.HttpMethod getRequestMethod()
    {
      return Request.HttpMethod.GET;
    }
  }

  public static GeminiApplication mockedApp()
  {
    GeminiApplication application = mock(GeminiApplication.class);

    when(application.getLog(anyString()))
        .then(invocation -> mock(ComponentLog.class));
    when(application.getConfigurator())
        .thenReturn(mock(Configurator.class));
    when(application.getFeatureManager())
        .thenReturn(mock(FeatureManager.class));
    when(application.getSimSessionManager())
        .then(invocation -> new SimSessionManager(application));
    when(application.getDefaultRequestCharset())
        .thenReturn(StandardCharsets.UTF_8);
    return application;
  }

  public static class ExampleHandler
      extends MethodSegmentHandler<Context>
  {
    public ExampleHandler(GeminiApplication app)
    {
      super(app);
    }

    @PathSegment("test")
    @Get
    public boolean doTest()
    {
      return text("Hello, World!");
    }

    @PathSegment("test2")
    public boolean doTest2()
    {
      return text("Hello, World! num: " + query().getInt("num"));
    }
  }
}
