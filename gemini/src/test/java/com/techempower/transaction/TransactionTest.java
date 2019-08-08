package com.techempower.transaction;

import com.techempower.util.Identifiable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import static org.junit.Assert.*;
import static com.techempower.transaction.TransactionTest.*;

@RunWith(TransactionTest.class)
@Suite.SuiteClasses({
    begin.class,
    commit.class,
    rollback.class
})
public class TransactionTest extends Suite
{
  public TransactionTest(Class<?> klass, RunnerBuilder builder) throws InitializationError
  {
    super(klass, builder);
  }

  static class House implements Identifiable
  {
    private TransactionalProperty<Long> id = new TransactionalProperty<>(0L);
    private TransactionalProperty<Integer> cityId  = new TransactionalProperty<>(0);
    private TransactionalProperty<String> dog = new TransactionalProperty<>();
    private TransactionalProperty<String> owner = new TransactionalProperty<>();

    public House()
    {
    }

    public House(long id)
    {
      setId(id);
    }

    @Override
    public long getId()
    {
      return id.get();
    }

    @Override
    public void setId(long id)
    {
      this.id.set(id);
    }

    public int getCityId()
    {
      return cityId.get();
    }

    public String getOwner()
    {
      return owner.get();
    }

    public House setOwner(String owner)
    {
      this.owner.set(owner);
      return this;
    }

    public House setCityId(int cityId)
    {
      this.cityId.set(cityId);
      return this;
    }

    public String getDog()
    {
      return dog.get();
    }

    public House setDog(String dog)
    {
      this.dog.set(dog);
      return this;
    }
  }

  public static class begin
  {
    @Test
    public void shouldCorrectlyManageTheThreadLocalUnderNormalCircumstances()
    {
      assertNull(Transaction.getCurrent());
      Transaction.begin(() -> assertNotNull(Transaction.getCurrent()));
      assertNull(Transaction.getCurrent());
    }

    @Test
    public void shouldCorrectlyManageTheThreadLocalIfAnExceptionIsThrown()
    {
      assertNull(Transaction.getCurrent());
      try
      {
        Transaction.begin(() -> {
          throw new RuntimeException();
        });
      }
      catch (RuntimeException ignored)
      {
      }
      finally
      {
        assertNull(Transaction.getCurrent());
      }
    }

  }

  public static class rollback
  {
    static class TestException extends RuntimeException
    {
    }

    @Test
    public void test()
    {
      House house = new House(4)
          .setCityId(10)
          .setOwner("Sally")
          .setDog("Tails");
      try
      {

        Transaction.begin(() -> {
          house.setDog("Sparky");
          throw new TestException();
        });
      }
      catch (TestException ignored)
      {
      }
      finally
      {
        assertEquals("Tails", house.getDog());
      }
    }
  }

  public static class commit
  {
    @Test
    public void test()
    {
      House house = new House(4)
          .setCityId(10)
          .setOwner("Sally")
          .setDog("Tails");
      try
      {
        Transaction.begin(() -> {
          house.setDog("Sparky");
        });
      }
      finally
      {
        // TODO: true -> make sure the values are part of the "main" values.
        //  ...Somehow.
        assertEquals("Sparky", house.getDog());
      }
    }

  }
}