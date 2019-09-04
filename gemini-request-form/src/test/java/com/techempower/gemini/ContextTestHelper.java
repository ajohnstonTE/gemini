package com.techempower.gemini;

import com.techempower.data.ConnectorFactory;
import com.techempower.gemini.context.Attachments;
import com.techempower.gemini.monitor.GeminiMonitor;
import com.techempower.gemini.mustache.MustacheManager;
import com.techempower.gemini.pyxis.BasicUser;
import com.techempower.gemini.session.SessionManager;
import com.techempower.gemini.simulation.GetSimRequest;
import com.techempower.gemini.simulation.ISimParameters;
import com.techempower.gemini.simulation.SimClient;
import com.techempower.gemini.simulation.SimParameters;
import com.techempower.log.ComponentLog;

public class ContextTestHelper
{
  private ContextTestHelper()
  {
  }

  public static Context context(String key, String value)
  {
    SimParameters parameters = new SimParameters();
    if (value != null)
    {
      parameters.append(key, value);
    }
    return context(parameters);
  }

  public static Context context(String key, String[] values)
  {
    SimParameters parameters = new SimParameters();
    if (values != null)
    {
      for (String value : values)
      {
        if (value != null)
        {
          parameters.append(key, value);
        }
      }
    }
    return context(parameters);
  }

  public static Context context(ISimParameters parameters)
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

      @Override
      public ComponentLog getLog(String componentCode)
      {
        return new ComponentLog(getApplicationLog(), componentCode)
        {
          @Override
          public void log(String logString, int debugLevel)
          {
          }

          @Override
          public void log(String logString)
          {
          }

          @Override
          public void log(String debugString, int debugLevel, Throwable exception)
          {
          }

          @Override
          public void log(String debugString, Throwable exception)
          {
          }
        };
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
    return new Context(application, request)
    {
      @Override
      public Attachments files()
      {
        return null;
      }
    };
  }
}
