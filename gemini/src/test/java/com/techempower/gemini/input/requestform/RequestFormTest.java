package com.techempower.gemini.input.requestform;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class RequestFormTest
{
  @Test
  public void testAnonymousSubclass()
  {
    class RequestFormSubclass extends RequestForm
    {
      public FormField field = new FormField<String>(this,"foo")
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
    public FormField field = new FormField<String>(this,"foo")
        .setRequired(true)
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
}