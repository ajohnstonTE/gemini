package com.techempower.gemini.input.requestform;

import com.techempower.data.ConnectorFactory;
import com.techempower.gemini.*;
import com.techempower.gemini.context.Attachments;
import com.techempower.gemini.input.Input;
import com.techempower.gemini.monitor.GeminiMonitor;
import com.techempower.gemini.mustache.MustacheManager;
import com.techempower.gemini.pyxis.BasicUser;
import com.techempower.gemini.session.SessionManager;
import com.techempower.gemini.simulation.GetSimRequest;
import com.techempower.gemini.simulation.SimClient;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestFormTest
{
  @Test
  public void testAnonymousSubclass()
  {
    class RequestFormSubclass extends RequestForm
    {
      public Field field = new Field<>(this,"foo", String.class)
          .setRequired(true)
          .setValueAccess(ValueAccess::getString, "dog");
    }
    RequestFormSubclass contract = new RequestFormSubclass();

    assertEquals(
        Collections.singletonList(contract.field),
        contract.getFields());
  }

  static class DeclaredRequestFormSubclass extends RequestForm
  {
    public Field<String> field = new Field<>(this, "foo", String.class)
        .setRequired(true)
        // TODO: Remove need for value access for base types.
        // TODO: Rename default value to something like 'nullDefault'
        .setValueAccess(ValueAccess::getString, "dog");
  }

  @Test
  public void testDeclaredSubclass()
  {

    DeclaredRequestFormSubclass contract = new DeclaredRequestFormSubclass();

    assertEquals(
        Collections.singletonList(contract.field),
        contract.getFields());
  }

  @Test
  public void doPerformanceTest()
  {
    doPerformanceTest(10_000);
    doPerformanceTest(100_000);
    doPerformanceTest(1_000_000);
    doPerformanceTest(10_000_000);
    doPerformanceTest(100_000_000);
    //doPerformanceTest(1_000_000_000);
  }

  private void doPerformanceTest(int rounds)
  {
    long start = System.currentTimeMillis();
    for (int i = 0; i < rounds; i++)
    {
      testAnonymousSubclass();
    }
    System.out.println(String.format(
        "Basic anonymous subclass took %sms do to %s round%s",
        System.currentTimeMillis() - start,
        rounds,
        rounds == 1 ? "" : "s"
    ));
    start = System.currentTimeMillis();
    for (int i = 0; i < rounds; i++)
    {
      testDeclaredSubclass();
    }
    System.out.println(String.format(
        "Basic declared subclass took %sms do to %s round%s",
        System.currentTimeMillis() - start,
        rounds,
        rounds == 1 ? "" : "s"
    ));
  }

  static class TestForm extends RequestForm
  {
    Field<Long> entityId = new Field<>(this, "entity-id", Long.class)
        .setRequired(true);
    Field<Double> doubleField = new NumberField<>(this, "double-field", Double.class)
        .setRequired(true)
        .setMin(4d)
        .setMax(10.5d);
  }

  @Test
  public void doTest()
  {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("entity-id", "7");
    parameters.put("double-field", "8");
    Context context = createContext(parameters);
    TestForm testForm = new TestForm();
    Input input = testForm.process(context);
    assertTrue(input.passed());
    assertEquals((Long)7L, testForm.entityId.getValue());
    assertEquals((Double) 8d, testForm.doubleField.getValue());
  }

  private Context createContext(Map<String, String> parameters)
  {
    GeminiApplication application = new GeminiApplication()
    {
      @Override
      protected Dispatcher constructDispatcher()
      {
        return null;
      }

      @Override
      protected ConnectorFactory constructConnectorFactory()
      {
        return null;
      }

      @Override
      protected MustacheManager constructMustacheManager()
      {
        return null;
      }

      @Override
      protected SessionManager constructSessionManager()
      {
        return null;
      }

      @Override
      protected GeminiMonitor constructMonitor()
      {
        return null;
      }

      @Override
      public Context getContext(Request request)
      {
        return null;
      }
    };
    Simulation simulation = new Simulation()
    {
      @Override
      public GeminiApplication getApplication()
      {
        return application;
      }

      @Override
      protected String getDocroot()
      {
        return "";
      }

      @Override
      protected Class<? extends BasicUser> getUserClass()
      {
        return null;
      }
    };
    SimClient simClient = new SimClient(1);
    Request request = new GetSimRequest(simulation, "", parameters, simClient,
        application);
    Context context = new Context(application, request)
    {
      @Override
      public Attachments files()
      {
        return null;
      }
    };
    return context;
  }
}