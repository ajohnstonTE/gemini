package com.techempower.gemini.rx;

import com.techempower.gemini.*;
import com.techempower.gemini.context.Attachments;
import com.techempower.gemini.exceptionhandler.BasicExceptionHandler;
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JaxRsDispatcherTest
{

  @Test
  void dispatch()
  {
    ExampleResource exampleResource = new ExampleResource();
    assertEquals("Hello, World!", exampleResource.doTest());
    assertEquals("Hello, World! num: 5", exampleResource.doTest2(5));
  }

  @Path("example")
  public static class ExampleResource
  {
    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String doTest()
    {
      return "Hello, World!";
    }

    @Path("test2")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String doTest2(@QueryParam("num") Integer num)
    {
      return "Hello, World! num: " + num;
    }
  }

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
            .add("example", new ExampleHandler(application))
            .add(new BasicExceptionHandler(application)));
    Simulation simulation = new StandardSimulation()
    {
      @Override
      public GeminiApplication getApplication()
      {
        return application;
      }
    };
    {
      SpyAwareContext context = spy(new SpyAwareContext(application,
          new AnotherGetSimRequest(
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
      SpyAwareContext context = spy(new SpyAwareContext(application,
          new AnotherGetSimRequest(
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

  @Test
  void dispatchTestTypical2()
  {
    // This is another painful approach to testing an endpoint. This is how you
    // test it "directly".
    GeminiApplication application = mockedApp();
    ExampleHandler exampleHandler = new ExampleHandler(application);
    Simulation simulation = new StandardSimulation()
    {
      @Override
      public GeminiApplication getApplication()
      {
        return application;
      }
    };
    {
      SpyAwareContext context = spy(new SpyAwareContext(application,
          new AnotherGetSimRequest(
              simulation,
              "example/test",
              new HashMap<>(),
              new SimClient(1),
              application)));
      context.setUp(context);
      ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
      exampleHandler.doTest();
      verify(context).print(argument.capture());
      assertEquals("Hello, World!", argument.getValue());
    }
    {
      SpyAwareContext context = spy(new SpyAwareContext(application,
          new AnotherGetSimRequest(
              simulation,
              "example/test2",
              new HashMap<String, String>()
              {{
                put("num", "5");
              }},
              new SimClient(1),
              application)));
      // It can't just pass in "this" in an initializer block because
      // that would refer to the non-spied context. It has to be the spied
      // one in order to be captured.
      context.setUp(context);
      ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
      exampleHandler.doTest2();
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
    public HttpMethod getRequestMethod()
    {
      return HttpMethod.GET;
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