package com.techempower.cache;

import com.techempower.cache.annotation.Indexed;
import com.techempower.util.Identifiable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityStoreTest
{
  @RegisterExtension
  public static EntityStoreTestResource entityStoreTestResource = EntityStoreTestResource.builder()
      .register(CacheGroup.of(House.class).maker(House::new))
      .register(CacheGroup.of(Doctor.class).maker(Doctor::new))
      .register(CacheGroup.of(Lawyer.class).maker(Lawyer::new))
      .setSqlScripts("sql/standard")
      .build();

  static class House implements Identifiable
  {
    private long id;
    private int cityId;
    private String dog;
    private String owner;

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
      return id;
    }

    @Override
    public void setId(long id)
    {
      this.id = id;
    }

    public int getCityId()
    {
      return cityId;
    }

    public String getOwner()
    {
      return owner;
    }

    public House setOwner(String owner)
    {
      this.owner = owner;
      return this;
    }

    public House setCityId(int cityId)
    {
      this.cityId = cityId;
      return this;
    }

    public String getDog()
    {
      return dog;
    }

    public House setDog(String dog)
    {
      this.dog = dog;
      return this;
    }
  }

  interface Person extends Identifiable
  {
    String getName();

    Person setName(String name);

    long getAge();

    Person setAge(long age);

    String getDog();

    Person setDog(String name);
  }

  interface Rich
  {
    long getSalary();
  }

  @Indexed
  static class Doctor implements Person, Rich
  {
    private long id;
    private String name;
    private long age;
    private String dog;
    private long numberOfLabCoats;
    private long salary;

    public Doctor()
    {
    }

    public Doctor(long id)
    {
      this.id = id;
    }

    @Override
    public long getId()
    {
      return id;
    }

    @Override
    public void setId(long id)
    {
      this.id = id;
    }

    @Override
    public String getName()
    {
      return name;
    }

    @Override
    public Doctor setName(String name)
    {
      this.name = name;
      return this;
    }

    @Override
    public long getAge()
    {
      return age;
    }

    @Override
    public Doctor setAge(long age)
    {
      this.age = age;
      return this;
    }

    @Override
    public String getDog()
    {
      return dog;
    }

    @Override
    public Doctor setDog(String dog)
    {
      this.dog = dog;
      return this;
    }

    public long getNumberOfLabCoats()
    {
      return numberOfLabCoats;
    }

    public Doctor setNumberOfLabCoats(long numberOfLabCoats)
    {
      this.numberOfLabCoats = numberOfLabCoats;
      return this;
    }

    @Override
    public long getSalary()
    {
      return salary;
    }

    public Doctor setSalary(long salary)
    {
      this.salary = salary;
      return this;
    }
  }

  @Indexed
  static class Lawyer implements Person, Rich
  {
    private long id;
    private String name;
    private long age;
    private String dog;
    private long numberOfSuits;
    private long salary;

    public Lawyer()
    {
    }

    public Lawyer(long id)
    {
      this.id = id;
    }

    @Override
    public long getId()
    {
      return id;
    }

    @Override
    public void setId(long id)
    {
      this.id = id;
    }

    @Override
    public String getName()
    {
      return name;
    }

    @Override
    public Lawyer setName(String name)
    {
      this.name = name;
      return this;
    }

    @Override
    public long getAge()
    {
      return age;
    }

    @Override
    public Lawyer setAge(long age)
    {
      this.age = age;
      return this;
    }

    @Override
    public String getDog()
    {
      return dog;
    }

    @Override
    public Lawyer setDog(String dog)
    {
      this.dog = dog;
      return this;
    }

    public long getNumberOfSuits()
    {
      return numberOfSuits;
    }

    public Lawyer setNumberOfSuits(long numberOfSuits)
    {
      this.numberOfSuits = numberOfSuits;
      return this;
    }

    @Override
    public long getSalary()
    {
      return salary;
    }

    public Lawyer setSalary(long salary)
    {
      this.salary = salary;
      return this;
    }
  }

  @DisplayName("Verify that the cache interacts with the database properly.")
  @Test
  void testBasicDbInteraction()
      throws Exception
  {
    House house = new House()
        .setCityId(10)
        .setOwner("Sally")
        .setDog("Tails");
    store().put(house);
    assertTrue(house.getId() != 0);
  }

  @DisplayName("Verify that the basic cache selectors work.")
  @Test
  void testBasicCacheSelectors()
  {
    List<Doctor> doctorsNamedMoe = store()
        .select(Doctor.class)
        .where(Doctor::getName, "getName").is("Moe")
        .list();
    assertEquals(new HashSet<>(Arrays.asList(3L, 6L, 8L)), doctorsNamedMoe
        .stream()
        .map(Doctor::getId)
        .collect(Collectors.toSet()));
    List<Lawyer> lawyersWithOneOrTwoSuits = store()
        .select(Lawyer.class)
        .where(Lawyer::getNumberOfSuits, "getNumberOfSuits").in(Arrays.asList(1L, 2L))
        .list();
    assertEquals(new HashSet<>(Arrays.asList(2L, 6L, 7L)), lawyersWithOneOrTwoSuits
        .stream()
        .map(Lawyer::getId)
        .collect(Collectors.toSet()));
  }

  @DisplayName("Verify that multiple classes can be selected on.")
  @Test
  void testMultiClassSelector()
  {
    List<? extends Person> peopleNamedMoeWithNoDogOrADogNamedPoppy = store()
        .select(Arrays.asList(Doctor.class, Lawyer.class))
        .where(Person::getName, "getName").is("Moe")
        .where(Person::getDog, "getDog").in(Arrays.asList(null, "Poppy"))
        .list();
    List<Person> peopleNamedMoeWithNoDogOrADogNamedPoppy2 = store()
        .selectAnySubclass(Person.class)
        .where(Person::getName, "getName").is("Moe")
        .where(Person::getDog, "getDog").in(Arrays.asList(null, "Poppy"))
        .list();
    Person personNamedMoeWithNoDogOrADogNamedPoppy = store()
        .select(Arrays.asList(Doctor.class, Lawyer.class))
        .where(Person::getName, "getName").is("Moe")
        .where(Person::getDog, "getDog").in(Arrays.asList(null, "Poppy"))
        .get();
    List<Rich> richPeople = store()
        .selectAnySubclass(Rich.class)
        .where(Rich::getSalary, "getSalary").in(Arrays.asList(405000L, 1000000L))
        .list();
    assertEquals(new HashSet<>(Arrays.asList(3L, 8L)), peopleNamedMoeWithNoDogOrADogNamedPoppy
        .stream()
        .filter(Doctor.class::isInstance)
        .map(Person::getId)
        .collect(Collectors.toSet()));
    assertEquals(new HashSet<>(Arrays.asList(3L, 8L)), peopleNamedMoeWithNoDogOrADogNamedPoppy2
        .stream()
        .filter(Doctor.class::isInstance)
        .map(Person::getId)
        .collect(Collectors.toSet()));
    assertEquals(new HashSet<>(Collections.singletonList(5L)), peopleNamedMoeWithNoDogOrADogNamedPoppy
        .stream()
        .filter(Lawyer.class::isInstance)
        .map(Person::getId)
        .collect(Collectors.toSet()));
    assertEquals(new HashSet<>(Arrays.asList(2L, 4L, 9L)), richPeople
        .stream()
        .filter(Doctor.class::isInstance)
        .map(Doctor.class::cast)
        .map(Person::getId)
        .collect(Collectors.toSet()));
    assertEquals(new HashSet<>(Collections.singletonList(7L)), richPeople
        .stream()
        .filter(Lawyer.class::isInstance)
        .map(Lawyer.class::cast)
        .map(Person::getId)
        .collect(Collectors.toSet()));
    boolean personIsOneOfTheDoctors = personNamedMoeWithNoDogOrADogNamedPoppy instanceof Doctor
        && Arrays.asList(3L, 8L).contains(personNamedMoeWithNoDogOrADogNamedPoppy.getId());
    boolean personIsOneOfTheLawyers = personNamedMoeWithNoDogOrADogNamedPoppy instanceof Lawyer
        && 5L == personNamedMoeWithNoDogOrADogNamedPoppy.getId();
    assertTrue(personIsOneOfTheDoctors || personIsOneOfTheLawyers);
  }

  private EntityStore store()
  {
    return entityStoreTestResource.store;
  }
}