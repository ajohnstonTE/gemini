package com.techempower.gemini.input.requestform;

import com.techempower.gemini.input.Input;
import com.techempower.gemini.input.processor.Uppercase;
import com.techempower.gemini.input.requestform.validator.TryCatchValidator;
import com.techempower.gemini.simulation.SimParameters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Objects;

import static com.techempower.gemini.ContextTestHelper.context;
import static org.junit.jupiter.api.Assertions.*;

public class RequestFormTest
{
  static class DeclaredRequestFormSubclass extends RequestForm
  {
    public Field<String> field = new BaseField<>(this, "foo", String.class)
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
  public <T> void testBaseTypes(Class<T> fieldType,
                                String inputValue,
                                T expected)
  {
    {
      class SingleFieldForm extends RequestForm
      {
        Field<T> field = new BaseField<>(this, "example", fieldType);
      }
      SingleFieldForm form = new SingleFieldForm();
      assertTrue(form.process(context("example", inputValue)).passed());
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
  public <T> void testBaseArrayTypes(Class<T> fieldType,
                                     String[] inputValues,
                                     T expected)
  {
    {
      class SingleFieldForm extends RequestForm
      {
        Field<T> field = new BaseField<>(this, "example", fieldType);
      }
      SingleFieldForm form = new SingleFieldForm();
      assertTrue(form.process(context("example", inputValues)).passed());
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
        assertTrue(form.process(context("example", "4")).failed());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(context("example", "2")).passed());
        assertEquals((Long) 2L, form.field.getValue());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(context("example", "0")).passed());
        assertEquals((Long) 0L, form.field.getValue());
      }
      {
        SingleLongNumberFieldForm form = new SingleLongNumberFieldForm();
        assertTrue(form.process(context("example", "0.0")).failed());
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
          assertTrue(form.process(context("example", "4")).passed());
          assertEquals((Float)4f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(context("example", "0.0")).passed());
          assertEquals((Float)0f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(context("example", "-0.1")).failed());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(context("example", "20")).passed());
          assertEquals((Float)20f, form.field.getValue());
        }
        {
          SingleFloatNumberFieldForm form = new SingleFloatNumberFieldForm();
          assertTrue(form.process(context("example", "2er0")).failed());
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
          assertTrue(form.process(context("example", "4")).passed());
          assertEquals((Double)4d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(context("example", "0.0")).passed());
          assertEquals((Double)0d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(context("example", "-0.1")).failed());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(context("example", "20")).passed());
          assertEquals((Double)20d, form.field.getValue());
        }
        {
          SingleDoubleNumberFieldForm form = new SingleDoubleNumberFieldForm();
          assertTrue(form.process(context("example", "2er0")).failed());
        }
      }
    }
  }

  @Test
  void testShortCircuitFields()
  {
    class TestForm extends RequestForm
    {
      Field<String> rawDate = new BaseField<>(this, "date", String.class)
          .addFieldValidator(new TryCatchValidator<>(LocalDate::parse, "`%s` is not a valid date"));
      Field<LocalDate> date = rawDate.derive(LocalDate::parse);
    }
    {
      TestForm form = new TestForm();
      Input input = form.process(context(new SimParameters()
          .append("date", "2019-01-03")));
      assertTrue(input.passed());
      assertEquals(LocalDate.of(2019, 1, 3), form.date.getValue());
    }
    {
      TestForm form = new TestForm();
      Input input = form.process(context(new SimParameters()
          .append("date", "2019-01-003")));
      assertTrue(input.failed());
      assertEquals(input.errors().size(), 1);
      assertEquals(input.errors().get(0), "`date` is not a valid date");
    }
  }

  @Test
  void testPerFieldValidation()
  {
    class TestForm extends RequestForm
    {
      Field<String> rawDate = new BaseField<>(this, "date", String.class)
          .addFieldValidator(new TryCatchValidator<>(LocalDate::parse, "`%s` is not a valid date"));
      Field<String> sortA = new BaseField<>(this, "sort", String.class)
          .addValidator(new Uppercase("sort"));
      Field<String> sortB = new BaseField<>(this, "sort", String.class);
    }
    {
      TestForm form = new TestForm();
      Input input = form.process(context(new SimParameters()
          .append("date", "2019-01-003")));
      assertTrue(input.failed());
      assertTrue(form.rawDate.input().failed());
      assertTrue(form.sortA.input().passed());
      assertTrue(form.sortB.input().passed());
    }
    {
      TestForm form = new TestForm();
      Input input = form.process(context(new SimParameters()
          .append("sort", "asc")));
      assertEquals("ASC", form.sortA.getValue());
      assertEquals("asc", form.sortB.getValue());
    }
  }
}