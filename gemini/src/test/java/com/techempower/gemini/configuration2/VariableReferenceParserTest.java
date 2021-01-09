package com.techempower.gemini.configuration2;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class VariableReferenceParserTest
{
  @RunWith(Parameterized.class)
  public static class parse
  {
    @Parameterized.Parameters(name = "{0}")
    public static Object[][] getParams()
    {
      return new Object[][]{
          new Object[]{
              "this is ${dog.bar} for $${dog.bar} for you",
              new Segment[]{
                  new TextSegment("this is "),
                  new ReferenceSegment("", "dog.bar"),
                  new TextSegment(" for "),
                  new TextSegment("$${dog.bar}"),
                  new TextSegment(" for you"),
              }
          },
          new Object[]{
              "this is ${dog.bar} for $${dog.bar}",
              new Segment[]{
                  new TextSegment("this is "),
                  new ReferenceSegment("", "dog.bar"),
                  new TextSegment(" for "),
                  new TextSegment("$${dog.bar}"),
              }
          },
          new Object[]{
              "${dog.bar} for $${dog.bar} for you",
              new Segment[]{
                  new ReferenceSegment("", "dog.bar"),
                  new TextSegment(" for "),
                  new TextSegment("$${dog.bar}"),
                  new TextSegment(" for you"),
              }
          },
          new Object[]{
              "${dog.bar}",
              new Segment[]{
                  new ReferenceSegment("", "dog.bar"),
              }
          },
          new Object[]{
              "this is ${dog.bar}",
              new Segment[]{
                  new TextSegment("this is "),
                  new ReferenceSegment("", "dog.bar"),
              }
          },
          new Object[]{
              "for you",
              new Segment[]{
                  new TextSegment("for you"),
              }
          },
          new Object[]{
              "$${dog.bar}",
              new Segment[]{
                  new TextSegment("$${dog.bar}"),
              }
          },
          new Object[]{
              "this is $${dog.bar} for you",
              new Segment[]{
                  new TextSegment("this is "),
                  new TextSegment("$${dog.bar}"),
                  new TextSegment(" for you"),
              }
          },
          new Object[]{
              "this is $${dog.bar}",
              new Segment[]{
                  new TextSegment("this is "),
                  new TextSegment("$${dog.bar}"),
              }
          },
          new Object[]{
              "$${dog.bar} for you",
              new Segment[]{
                  new TextSegment("$${dog.bar}"),
                  new TextSegment(" for you"),
              }
          },
          new Object[]{
              "a${dog.bar}b",
              new Segment[]{
                  new TextSegment("a"),
                  new ReferenceSegment("", "dog.bar"),
                  new TextSegment("b"),
              }
          },
          new Object[]{
              "a${dog.bar}",
              new Segment[]{
                  new TextSegment("a"),
                  new ReferenceSegment("", "dog.bar"),
              }
          },
          new Object[]{
              "${dog.bar}b",
              new Segment[]{
                  new ReferenceSegment("", "dog.bar"),
                  new TextSegment("b"),
              }
          },
          new Object[]{
              "a$${dog.bar}b",
              new Segment[]{
                  new TextSegment("a"),
                  new TextSegment("$${dog.bar}"),
                  new TextSegment("b"),
              }
          },
          new Object[]{
              "a$${dog.bar}",
              new Segment[]{
                  new TextSegment("a"),
                  new TextSegment("$${dog.bar}"),
              }
          },
          new Object[]{
              "$${dog.bar}b",
              new Segment[]{
                  new TextSegment("$${dog.bar}"),
                  new TextSegment("b"),
              }
          },
          new Object[]{
              "{dog.bar}",
              new Segment[]{
                  new TextSegment("{dog.bar}"),
              }
          },
          new Object[]{
              "${dog.bar}",
              new Segment[]{
                  new ReferenceSegment("", "dog.bar"),
              }
          },
          new Object[]{
              "$${dog.bar}",
              new Segment[]{
                  new TextSegment("$${dog.bar}"),
              }
          },
          new Object[]{
              "$$${dog.bar}",
              new Segment[]{
                  new ReferenceSegment("$$", "dog.bar"),
              }
          },
          new Object[]{
              "$$$${dog.bar}",
              new Segment[]{
                  new TextSegment("$$$${dog.bar}"),
              }
          },
          new Object[]{
              "$$$$${dog.bar}",
              new Segment[]{
                  new ReferenceSegment("$$$$", "dog.bar"),
              }
          },
          new Object[]{
              "$$$$$${dog.bar}",
              new Segment[]{
                  new TextSegment("$$$$$${dog.bar}"),
              }
          },
          new Object[]{
              "$$$$$$${dog.bar}",
              new Segment[]{
                  new ReferenceSegment("$$$$$$", "dog.bar"),
              }
          },
      };
    }

    @Parameterized.Parameter
    public String str;

    @Parameterized.Parameter(1)
    public Segment[] expected;

    @Test()
    public void test() throws Exception
    {
      assertEquals(Arrays.asList(expected),
          new VariableReferenceParser().parse(str));
    }
  }
}
