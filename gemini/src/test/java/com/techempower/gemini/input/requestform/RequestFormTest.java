package com.techempower.gemini.input.requestform;

import com.techempower.data.ConnectorFactory;
import com.techempower.gemini.*;
import com.techempower.gemini.context.Attachments;
import com.techempower.gemini.monitor.GeminiMonitor;
import com.techempower.gemini.mustache.MustacheManager;
import com.techempower.gemini.pyxis.BasicUser;
import com.techempower.gemini.session.SessionManager;
import com.techempower.gemini.simulation.GetSimRequest;
import com.techempower.gemini.simulation.SimClient;
import com.techempower.gemini.simulation.SimParameters;
import com.techempower.log.ComponentLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class RequestFormTest
{
  static class DeclaredRequestFormSubclass extends RequestForm
  {
    public Field<String> field = new Field<>(this, "foo", String.class)
        .setRequired(true)
        .setValueAccess(ValueAccess::getString, "dog");
  }

  public static Object[] testBaseTypeParams()
  {
    return new Object[][]{
        {Long.class, "7", 7L},
        {Long.class, null, null},
        {Long.class, "", null},
        {Long.class, "7.7", null},
        {Long.class, "0", 0L},
        {Long.class, "0.0", null},
        {Long.class, "3.0", null},
        {Long.class, "-1", -1L},
        {Long.class, String.valueOf(Long.MAX_VALUE), Long.MAX_VALUE},

        {Integer.class, "7", 7},
        {Integer.class, null, null},
        {Integer.class, "", null},
        {Integer.class, "7.7", null},
        {Integer.class, "0", 0},
        {Integer.class, "0.0", null},
        {Integer.class, "3.0", null},
        {Integer.class, "-1", -1},
        {Integer.class, String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE},
        {Integer.class, String.valueOf(Long.MAX_VALUE), null},

        {Float.class, "7", 7f},
        {Float.class, null, null},
        {Float.class, "", null},
        {Float.class, "7.7", 7.7f},
        {Float.class, "0", 0f},
        {Float.class, "0.0", 0.0f},
        {Float.class, "3.0", 3.0f},
        {Float.class, "-1", -1f},
        {Float.class, String.valueOf(Float.MAX_VALUE), Float.MAX_VALUE},
        {Float.class, String.valueOf(Long.MAX_VALUE), (float) Long.MAX_VALUE},

        {Double.class, "7", 7d},
        {Double.class, null, null},
        {Double.class, "", null},
        {Double.class, "7.7", 7.7d},
        {Double.class, "0", 0d},
        {Double.class, "0.0", 0.0d},
        {Double.class, "3.0", 3.0d},
        {Double.class, "-1", -1d},
        {Double.class, String.valueOf(Double.MAX_VALUE), Double.MAX_VALUE},
        {Double.class, String.valueOf(Long.MAX_VALUE), (double) Long.MAX_VALUE},

        {Byte.class, "7", (byte) 7},
        {Byte.class, null, null},
        {Byte.class, "", null},
        {Byte.class, "7.7", null},
        {Byte.class, "0", (byte) 0},
        {Byte.class, "0.0", null},
        {Byte.class, "3.0", null},
        {Byte.class, "-1", (byte) -1},
        {Byte.class, String.valueOf(Byte.MAX_VALUE), Byte.MAX_VALUE},
        {Byte.class, String.valueOf(Long.MAX_VALUE), null},

        {Short.class, "7", (short) 7},
        {Short.class, null, null},
        {Short.class, "", null},
        {Short.class, "7.7", null},
        {Short.class, "0", (short) 0},
        {Short.class, "0.0", null},
        {Short.class, "3.0", null},
        {Short.class, "-1", (short) -1},
        {Short.class, String.valueOf(Short.MAX_VALUE), Short.MAX_VALUE},
        {Short.class, String.valueOf(Long.MAX_VALUE), null},

        {String.class, "7", "7"},
        {String.class, null, null},
        {String.class, "", ""},
        {String.class, "7.7", "7.7"},
        {String.class, "0", "0"},
        {String.class, "0.0", "0.0"},
        {String.class, "3.0", "3.0"},
        {String.class, "-1", "-1"},
        {String.class, String.valueOf(Long.MAX_VALUE), String.valueOf(Long.MAX_VALUE)},

        {Boolean.class, "true", true},
        {Boolean.class, "yes", true},
        {Boolean.class, "1", true},
        {Boolean.class, "y", true},
        {Boolean.class, "on", true},
        {Boolean.class, "false", false},
        {Boolean.class, "no", false},
        {Boolean.class, "0", false},
        {Boolean.class, "n", false},
        {Boolean.class, "off", false},
        {Boolean.class, null, null},
        {Boolean.class, "", null},
        {Boolean.class, " ", null},
        {Boolean.class, "7.7", null},
        {Boolean.class, "0.0", null},
        {Boolean.class, "3.0", null},
        {Boolean.class, "-1", null},
    };
  }

  @ParameterizedTest
  @MethodSource("testBaseTypeParams")
  public <T> void testBaseTypes(Class<T> fieldType, String inputValue, T expected)
  {
    {
      class SingleFieldForm extends RequestForm
      {
        Field<T> field = new Field<>(this, "example", fieldType);
      }
      SingleFieldForm form = new SingleFieldForm();
      assertTrue(form.process(ctx("example", inputValue)).passed());
      assertEquals(expected, form.field.getValue());
    }
  }

  public static Object[] testBaseArrayTypeParams()
  {
    return new Object[][]{
        {String[].class, new String[]{"true", "false"}, new String[]{"true", "false"}},
        {String[].class, new String[]{"0", "1"}, new String[]{"0", "1"}},
        {String[].class, new String[]{"null", ""}, new String[]{"null", ""}},
        {String[].class, new String[]{"null", "21"}, new String[]{"null", "21"}},
        {String[].class, new String[]{"null", "2.1"}, new String[]{"null", "2.1"}},
        {String[].class, new String[]{"null", "-4"}, new String[]{"null", "-4"}},
        {String[].class, null, new String[0]},
        {String[].class, new String[0], new String[0]},

        {int[].class, new String[]{"true", "false"}, new int[]{0, 0}},
        {int[].class, new String[]{"0", "1"}, new int[]{0, 1}},
        {int[].class, new String[]{"null", ""}, new int[]{0, 0}},
        {int[].class, new String[]{"null", "21"}, new int[]{0, 21}},
        {int[].class, new String[]{"null", "2.1"}, new int[]{0, 0}},
        {int[].class, new String[]{"null", "-4"}, new int[]{0, -4}},
        {int[].class, null, new int[0]},
        {int[].class, new String[0], new int[0]},

        {long[].class, new String[]{"true", "false"}, new long[]{0, 0}},
        {long[].class, new String[]{"0", "1"}, new long[]{0, 1}},
        {long[].class, new String[]{"null", ""}, new long[]{0, 0}},
        {long[].class, new String[]{"null", "21"}, new long[]{0, 21}},
        {long[].class, new String[]{"null", "2.1"}, new long[]{0, 0}},
        {long[].class, new String[]{"null", "-4"}, new long[]{0, -4}},
        {long[].class, null, new long[0]},
        {long[].class, new String[0], new long[0]},
    };
  }

  @ParameterizedTest
  @MethodSource("testBaseArrayTypeParams")
  public <T> void testBaseArrayTypes(Class<T> fieldType, String[] inputValues, T expected)
  {
    {
      class SingleFieldForm extends RequestForm
      {
        Field<T> field = new Field<>(this, "example", fieldType);
      }
      SingleFieldForm form = new SingleFieldForm();
      assertTrue(form.process(ctx("example", inputValues)).passed());
      assertTrue(Objects.deepEquals(expected, form.field.getValue()));
    }
  }

  @Test
  public void testNumberField()
  {
    {
      class SingleLongNumberFieldForm extends RequestForm
      {
        Field<Long> field = new NumberField<>(this, "example", Long.class)
            .setMin(0L)
            .setMax(2L);
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(ctx("example", "4")).failed());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(ctx("example", "2")).passed());
        assertEquals((Long) 2L, form.field.getValue());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(ctx("example", "0")).passed());
        assertEquals((Long) 0L, form.field.getValue());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(ctx("example", "0.0")).failed());
      }
    }
    {
      class SingleFloatNumberFieldForm extends RequestForm
      {
        Field<Float> field = new NumberField<>(this, "example", Float.class)
            .setMax(1.5f)
            .setMin(20f);
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(ctx("example", "4")).passed());
          assertEquals((Float)4f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(ctx("example", "0.0")).passed());
          assertEquals((Float)0f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(ctx("example", "-0.1")).failed());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(ctx("example", "20")).passed());
          assertEquals((Float)20f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(ctx("example", "2er0")).failed());
        }
      }
    }
    {
      class SingleDoubleNumberFieldForm extends RequestForm
      {
        Field<Double> field = new NumberField<>(this, "example", Double.class)
            .setMax(1.5d)
            .setMin(20d);
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(ctx("example", "4")).passed());
          assertEquals((Double)4d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(ctx("example", "0.0")).passed());
          assertEquals((Double)0d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(ctx("example", "-0.1")).failed());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(ctx("example", "20")).passed());
          assertEquals((Double)20d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(ctx("example", "2er0")).failed());
        }
      }
    }
  }

  private Context ctx(String key, String value)
  {
    SimParameters parameters = new SimParameters();
    if (value != null)
    {
      parameters.append(key, value);
    }
    return context(parameters);
  }

  private Context ctx(String key, String[] values)
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

  private Context context(SimParameters parameters)
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
        return new ComponentLog(getApplicationLog(), componentCode) {
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