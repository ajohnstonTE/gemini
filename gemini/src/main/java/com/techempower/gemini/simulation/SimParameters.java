package com.techempower.gemini.simulation;

import com.google.common.net.UrlEscapers;

import java.util.*;
import java.util.function.BiConsumer;

public class SimParameters
  implements ISimParameters
{
  /// Note: Should this be thread-safe and use CopyOnWriteArrayList?
  private List<Entry> internalParameters = new ArrayList<>();

  public SimParameters()
  {
  }

  public SimParameters(Map<String, String> parameters)
  {
    parameters.forEach(this::append);
  }

  @Override
  public ISimParameters append(String name, String value)
  {
    Objects.requireNonNull(name);
    Objects.requireNonNull(value);
    internalParameters.add(new Entry(name, value));
    return this;
  }

  @Override
  public ISimParameters delete(String name)
  {
    Objects.requireNonNull(name);
    internalParameters
        .removeIf(entry -> entry.getName().equals(name));
    return this;
  }

  @Override
  public Iterator<Entry> entries()
  {
    return internalParameters.iterator();
  }

  @Override
  public ISimParameters forEach(BiConsumer<String, String> consumer)
  {
    Objects.requireNonNull(consumer);
    internalParameters
        .forEach(entry -> consumer.accept(entry.getName(), entry.getValue()));
    return this;
  }

  @Override
  public String get(String name)
  {
    Objects.requireNonNull(name);
    return internalParameters
        .stream()
        .filter(entry -> entry.getName().equals(name))
        .map(Entry::getValue)
        .findFirst()
        .orElse(null);
  }

  @Override
  public String[] getAll(String name)
  {
    Objects.requireNonNull(name);
    return internalParameters
        .stream()
        .filter(entry -> entry.getName().equals(name))
        .map(Entry::getValue)
        .toArray(String[]::new);
  }

  @Override
  public boolean has(String name)
  {
    Objects.requireNonNull(name);
    return internalParameters
        .stream()
        .anyMatch(entry -> entry.getName().equals(name));
  }

  @Override
  public Iterator<String> keys()
  {
    return new Iterator<String>() {
      private Iterator<Entry> entryIterator = entries();
      @Override
      public boolean hasNext()
      {
        return entryIterator.hasNext();
      }

      @Override
      public String next()
      {
        return entryIterator.next().getName();
      }

      @Override
      public void remove()
      {
        entryIterator.remove();
      }
    };
  }

  @Override
  public ISimParameters set(String name, String value)
  {
    Objects.requireNonNull(name);
    Objects.requireNonNull(value);
    return this;
  }

  @Override
  public Iterator<String> values()
  {
    return new Iterator<String>() {
      private Iterator<Entry> entryIterator = entries();
      @Override
      public boolean hasNext()
      {
        return entryIterator.hasNext();
      }

      @Override
      public String next()
      {
        return entryIterator.next().getValue();
      }

      @Override
      public void remove()
      {
        entryIterator.remove();
      }
    };
  }

  @Override
  public String toString()
  {
    if (internalParameters.isEmpty())
    {
      return "";
    }
    List<String> pairs = new ArrayList<>();
    forEach((name, value) -> pairs.add(
        UrlEscapers.urlPathSegmentEscaper().escape(name) + "="
            + UrlEscapers.urlPathSegmentEscaper().escape(value)));
    return "?" + String.join("&", pairs);
  }
}
