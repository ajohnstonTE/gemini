package com.techempower.transaction;

import com.techempower.util.Identifiable;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionTest
{
  @Container
  public static MySQLContainer container = new MySQLContainer();

  void createTable()
      throws Exception
  {
    String jdbcUrl = container.getJdbcUrl();
    String username = container.getUsername();
    String password = container.getPassword();
    try (Connection conn = DriverManager
        .getConnection(jdbcUrl, username, password))
    {
      conn.createStatement().execute(
          "CREATE TABLE `house` ("
              + " `id` INTEGER AUTO_INCREMENT PRIMARY KEY,"
              + " `cityId` INTEGER NOT NULL,"
              + " `owner` VARCHAR(255) NOT NULL,"
              + " `dog` VARCHAR(255),"
              + " UNIQUE KEY `unique_city_and_owner` (`cityId`, `owner`)"
              + ")");
    }
  }

  void dropTable()
      throws Exception
  {
    String jdbcUrl = container.getJdbcUrl();
    String username = container.getUsername();
    String password = container.getPassword();

    try (Connection conn = DriverManager
        .getConnection(jdbcUrl, username, password))
    {
      conn.createStatement().execute("DROP TABLE `house`;");
    }
  }

  static class House implements Identifiable
  {
    private TransactionalProperty<Long> id = new TransactionalProperty<>(0L);
    private TransactionalProperty<Integer> cityId = new TransactionalProperty<>(0);
    private TransactionalProperty<String> owner = new TransactionalProperty<>();
    private TransactionalProperty<String> dog = new TransactionalProperty<>();

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

    public House setCityId(int cityId)
    {
      this.cityId.set(cityId);
      return this;
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

  @Nested
  public class begin
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

  @Nested
  public class rollback
  {
    class TestException extends RuntimeException
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

  @Nested
  public class commit
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

  @Nested
  @DisplayName("should work correctly with the database")
  public class sql
  {
    @BeforeEach
    public void setUpEach()
        throws Exception
    {
      TransactionTest.this.createTable();
    }

    @AfterEach
    public void tearDownEach()
        throws Exception
    {
      TransactionTest.this.dropTable();
    }

    @Test
    @DisplayName("when in normal conditions")
    public void normalConditions()
        throws Exception
    {
      String jdbcUrl = container.getJdbcUrl();
      String username = container.getUsername();
      String password = container.getPassword();
      try (Connection conn = DriverManager
          .getConnection(jdbcUrl, username, password))
      {
        try
        {
          conn.setAutoCommit(false);
          try (PreparedStatement statement = conn.prepareStatement(
              "INSERT INTO `house` " +
                  "(`cityId`, `owner`, `dog`) " +
                  "VALUES (?, ?, ?)"))
          {
            attachArguments(statement, 10, "Sally", "Tails");
            statement.executeUpdate();
          }
          conn.commit();
        }
        catch (Exception e)
        {
          conn.rollback();
        }
      }
      finally
      {
        try (Connection conn = DriverManager
            .getConnection(jdbcUrl, username, password);
             PreparedStatement statement = conn.prepareStatement(
                 "SELECT COUNT(*) FROM `house`"))
        {
          ResultSet resultSet = statement.executeQuery();
          assertTrue(resultSet.next());
          int count = resultSet.getInt(1);
          assertEquals(1, count);
        }
      }
    }

    @Test
    @DisplayName("when a sql exception is thrown by rolling back the changes")
    public void sqlExceptionThrown()
        throws Exception
    {
      String jdbcUrl = container.getJdbcUrl();
      String username = container.getUsername();
      String password = container.getPassword();
      try (Connection conn = DriverManager
          .getConnection(jdbcUrl, username, password))
      {
        try
        {
          conn.setAutoCommit(false);
          try (PreparedStatement statement = conn.prepareStatement(
              "INSERT INTO `house` " +
                  "(`cityId`, `owner`, `dog`) " +
                  "VALUES (?, ?, ?)"))
          {
            attachArguments(statement, 10, "Sally", "Tails");
            statement.executeUpdate();
          }
          try (PreparedStatement statement = conn.prepareStatement(
              "INSERT INTO `house` " +
                  "(`cityId`, `owner`, `dog`) " +
                  "VALUES (?, ?, ?)"))
          {
            attachArguments(statement, 10, "Sally", "Whales");
            statement.executeUpdate();
          }
          conn.commit();
        }
        catch (Exception e)
        {
          conn.rollback();
        }
      }
      finally
      {
        try (Connection conn = DriverManager
            .getConnection(jdbcUrl, username, password);
             PreparedStatement statement = conn.prepareStatement(
                 "SELECT COUNT(*) FROM `house`"))
        {
          ResultSet resultSet = statement.executeQuery();
          assertTrue(resultSet.next());
          int count = resultSet.getInt(1);
          assertEquals(0, count);
        }
      }
    }

    /**
     * Attach an arbitrary list of arguments to a PreparedStatement.
     * <p>
     * Copied from EntityGroup for now.
     */
    private void attachArguments(PreparedStatement statement, Object... arguments)
        throws SQLException
    {
      int index = 1;
      for (Object argument : arguments)
      {
        if (argument instanceof Date)
        {
          statement.setDate(index++, new java.sql.Date(((Date) argument).getTime()));
        }
        else
        {
          statement.setObject(index++, argument);
        }
      }
    }
  }
}