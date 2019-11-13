package com.techempower.gemini.params;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ParametersTest
{

  @Test
  void values()
  {
    {
      assertIterableEquals(
          Collections.singleton(Collections.singletonList("bar")),
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .values());
    }
    {
      assertArrayEquals(
          new List[] { Collections.singletonList("bar") },
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .values()
              .toArray());
    }
    {
      assertArrayEquals(
          new List[] { Collections.singletonList("bar") },
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .values()
              .toArray(new List[0]));
    }
  }

  @Test
  void entrySet()
  {
    {
      assertIterableEquals(
          Collections.singleton(new TestEntry("foo", "bar")),
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .entrySet());
    }
    {
      assertArrayEquals(
          new Map.Entry[] { new TestEntry("foo", "bar") },
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .entrySet()
              .toArray());
    }
    {
      assertArrayEquals(
          new Map.Entry[] { new TestEntry("foo", "bar") },
          Parameters.builder()
              .append("foo", "bar")
              .build()
              .entrySet()
              .toArray(new Map.Entry[0]));
    }
  }

  static class TestEntry
      implements Map.Entry<String, List<String>>
  {
    private final String       key;
    private final List<String> value;

    TestEntry(String key, List<String> value)
    {
      this.key = key;
      this.value = value;
    }

    TestEntry(String key, String... value)
    {
      this(key, Arrays.asList(value));
    }

    @Override
    public String getKey()
    {
      return key;
    }

    @Override
    public List<String> getValue()
    {
      return value;
    }

    @Override
    public List<String> setValue(List<String> value)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (!(o instanceof Map.Entry)) return false;
      Map.Entry testEntry = (Map.Entry) o;
      return Objects.equals(getKey(), testEntry.getKey()) &&
          Objects.equals(getValue(), testEntry.getValue());
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(getKey(), getValue());
    }

    @Override
    public String toString()
    {
      return "TestEntry{" +
          "key='" + key + '\'' +
          ", value=" + value +
          '}';
    }
  }
}