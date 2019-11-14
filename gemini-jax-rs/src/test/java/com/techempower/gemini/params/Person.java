package com.techempower.gemini.params;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"id", "name", "age", "abbr"})
public class Person
{
  private long id;
  private String name;
  private int age;
  private String abbr;

  public long getId()
  {
    return id;
  }

  public Person setId(long id)
  {
    this.id = id;
    return this;
  }

  public String getName()
  {
    return name;
  }

  public Person setName(String name)
  {
    this.name = name;
    return this;
  }

  public int getAge()
  {
    return age;
  }

  public Person setAge(int age)
  {
    this.age = age;
    return this;
  }

  public String getAbbr()
  {
    return abbr;
  }

  public Person setAbbr(String abbr)
  {
    this.abbr = abbr;
    return this;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return getId() == person.getId() &&
        getAge() == person.getAge() &&
        Objects.equals(getName(), person.getName()) &&
        Objects.equals(getAbbr(), person.getAbbr());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getId(), getName(), getAge(), getAbbr());
  }

  @Override
  public String toString()
  {
    return "Person{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", age=" + age +
        ", abbr='" + abbr + '\'' +
        '}';
  }
}
