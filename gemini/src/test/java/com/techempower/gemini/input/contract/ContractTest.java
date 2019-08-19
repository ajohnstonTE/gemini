package com.techempower.gemini.input.contract;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ContractTest
{
  @Test
  public void testAnonymousSubclass()
  {
    class ContractSubclass extends Contract
    {
      public ContractField field = new ContractField<String>(this,"foo")
          .setRequired(true)
          .setValueAccess(ContractFieldValues::getString, "dog");
    }
    ContractSubclass contract = new ContractSubclass();

    assertEquals(
        Collections.singletonList(contract.field),
        contract.getFields());
  }

  static class DeclaredContractSubclass extends Contract
  {
    public ContractField field = new ContractField<String>(this,"foo")
        .setRequired(true)
        .setValueAccess(ContractFieldValues::getString, "dog");
  }

  @Test
  public void testDeclaredSubclass()
  {

    DeclaredContractSubclass contract = new DeclaredContractSubclass();

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