package com.techempower.gemini.params;

import com.google.common.collect.LinkedListMultimap;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Parameters
    implements IParameters
{
  private final LinkedListMultimap<String, String> internal;

  public Parameters()
  {
    internal = LinkedListMultimap.create();
  }

  /**
   * Constructs an empty Parameters with enough capacity to hold the
   * specified number of keys without rehashing
   */
  public Parameters(int expectedKeys)
  {
    internal = LinkedListMultimap.create(expectedKeys);
  }

  protected Parameters(Parameters parameters)
  {
    internal = LinkedListMultimap.create(parameters.internal);
  }

  public Parameters(Map<String, String> parameters)
  {
    this(parameters.size());
    parameters.forEach(this::add);
  }

  @Override
  public void putSingle(String key, String value)
  {
    internal.replaceValues(key, Collections.singletonList(value));
  }

  @Override
  public void add(String key, String value)
  {
    internal.put(key, value);
  }

  @Override
  public String getFirst(String name)
  {
    Objects.requireNonNull(name);
    List<String> values = internal.get(name);
    return values != null && !values.isEmpty() ? values.get(0) : null;
  }

  @Override
  public void addAll(String key, String... newValues)
  {
    Objects.requireNonNull(key);
    addAll(key, Arrays.asList(newValues));
  }

  @Override
  public void addAll(String key, List<String> valueList)
  {
    Objects.requireNonNull(key);
    internal.putAll(key, valueList);
  }

  @Override
  public void addFirst(String key, String value)
  {
    if (containsKey(key))
    {
      get(key).add(0, value);
    }
    else
    {
      add(key, value);
    }
  }

  @Override
  public boolean equalsIgnoreValueOrder(MultivaluedMap<String, String> otherMap)
  {
    return keySet()
        .stream()
        .allMatch(key -> {
          if (!otherMap.containsKey(key))
          {
            return false;
          }
          List<String> otherValues = otherMap.get(key);
          if (otherValues == null)
          {
            return false;
          }
          List<String> values = new ArrayList<>(get(key));
          List<String> otherValuesCopy = new ArrayList<>(otherValues);
          values.sort(Comparator.naturalOrder());
          otherValuesCopy.sort(Comparator.naturalOrder());
          return values.equals(otherValuesCopy);
        });
  }

  @Override
  public int size()
  {
    return internal.size();
  }

  @Override
  public boolean isEmpty()
  {
    return internal.isEmpty();
  }

  @Override
  public boolean containsKey(Object name)
  {
    Objects.requireNonNull(name);
    return internal.containsKey(name);
  }

  @Override
  public boolean containsValue(Object value)
  {
    Objects.requireNonNull(value);
    return internal.containsValue(value);
  }

  @Override
  public List<String> get(Object key)
  {
    Objects.requireNonNull(key);
    if (!(key instanceof String) || !containsKey(key))
    {
      return null;
    }
    return internal.get((String) key);
  }

  @Override
  public List<String> put(String key, List<String> value)
  {
    if (!containsKey(key))
    {
      internal.putAll(key, value);
      return null;
    }
    return internal.replaceValues(key, value);
  }

  @Override
  public List<String> remove(Object key)
  {
    if (!(key instanceof String) || !containsKey(key))
    {
      return null;
    }
    return internal.removeAll(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends List<String>> m)
  {
    m.forEach(internal::replaceValues);
  }

  @Override
  public void clear()
  {
    internal.clear();
  }

  @Override
  public Set<String> keySet()
  {
    return internal.keySet();
  }

  @Override
  public Collection<List<String>> values()
  {
    return new SyncValues(this);
  }

  @Override
  public Set<Entry<String, List<String>>> entrySet()
  {
    return new SyncEntries(this);
  }

  @Override
  public void forEachFlat(BiConsumer<String, String> consumer)
  {
    Objects.requireNonNull(consumer);
    internal.entries()
        .forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
  }

  @Override
  public Map<String, String> compress()
  {
    Map<String, String> parameters = new HashMap<>();
    forEachFlat(parameters::putIfAbsent);
    return parameters;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Parameters that = (Parameters) o;
    return internal.equals(that.internal);
  }

  @Override
  public int hashCode()
  {
    return internal.hashCode();
  }

  @Override
  public String toString()
  {
    return "Parameters{" +
        entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(";")) +
        '}';
  }

  public static ParametersBuilder builder()
  {
    return new ParametersBuilder();
  }

  public static class ParametersBuilder
  {
    private final Parameters parameters = new Parameters();

    /**
     * Appends a specified key/value pair as a new search parameter.
     *
     * @param name  - The name of the parameter to append.
     * @param value - The value of the parameter to append.
     * @return a reference to this builder object, for chaining
     */
    public ParametersBuilder append(String name, String value)
    {
      Objects.requireNonNull(name);
      Objects.requireNonNull(value);
      parameters.add(name, value);
      return this;
    }

    public Parameters build()
    {
      return new Parameters(parameters);
    }
  }

  private static class SyncValues
      implements Collection<List<String>>
  {
    private final Parameters parameters;

    private SyncValues(Parameters parameters)
    {
      this.parameters = parameters;
    }

    @Override
    public int size()
    {
      return parameters.keySet().size();
    }

    @Override
    public boolean isEmpty()
    {
      return parameters.keySet().isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
      if (!(o instanceof List))
      {
        return false;
      }
      return parameters.keySet()
          .stream()
          .map(parameters::get)
          .anyMatch(o::equals);
    }

    @Override
    public Iterator<List<String>> iterator()
    {
      return new SyncValuesIterator(this);
    }

    @Override
    public Object[] toArray()
    {
      return parameters.keySet()
          .stream()
          .map(parameters::get)
          .toArray();
    }

    // TODO: Check this out.
    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a)
    {
      return parameters.keySet()
          .stream()
          .map(parameters::get)
          .collect(Collectors.toList())
          .toArray(a);
    }

    @Override
    public boolean add(List<String> strings)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
      if (o instanceof List && parameters.containsValue(o))
      {
        parameters.internal.entries()
            .stream()
            .filter(entry -> entry.getValue().equals(o))
            .map(Entry::getKey)
            .forEach(parameters::remove);
        return true;
      }
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
      return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends List<String>> c)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
      return c.stream()
          .map(this::remove)
          .reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
      return parameters.entrySet()
          .stream()
          .filter(entry -> !c.contains(entry.getValue()))
          .map(Entry::getKey)
          .map(parameters::remove)
          .count() > 0;
    }

    @Override
    public void clear()
    {
      parameters.clear();
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }
      SyncValues lists = (SyncValues) o;
      return Objects.equals(parameters, lists.parameters);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(parameters);
    }

    @Override
    public String toString()
    {
      return "SyncValues{" +
          parameters +
          '}';
    }

    private static class SyncValuesIterator
        implements Iterator<List<String>>
    {
      private final SyncValues       syncValues;
      private final Iterator<String> keys;

      private SyncValuesIterator(SyncValues syncValues)
      {
        this.syncValues = syncValues;
        keys = syncValues.parameters.keySet().iterator();
      }

      @Override
      public boolean hasNext()
      {
        return keys.hasNext();
      }

      @Override
      public List<String> next()
      {
        return syncValues.parameters.get(keys.next());
      }

      @Override
      public void remove()
      {
        keys.remove();
      }
    }
  }

  private static class SyncEntries
      implements Set<Entry<String, List<String>>>
  {

    private final Parameters parameters;

    private SyncEntries(Parameters parameters)
    {
      this.parameters = parameters;
    }

    @Override
    public int size()
    {
      return parameters.internal.keySet().size();
    }

    @Override
    public boolean isEmpty()
    {
      return parameters.internal.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
      if (!(o instanceof Entry))
      {
        return false;
      }
      Entry<?, ?> entry = (Entry) o;
      Object key = entry.getKey();
      Object value = entry.getValue();
      if (!(key instanceof String) || !(value instanceof List))
      {
        return false;
      }
      return parameters.internal.containsKey(key)
          && parameters.internal.get((String) key).equals(value);
    }

    @Override
    public Iterator<Entry<String, List<String>>> iterator()
    {
      return new EntriesIterator(this);
    }

    @Override
    public Object[] toArray()
    {
      return parameters.keySet()
          .stream()
          .map(key -> new EntriesEntry(this, key))
          .toArray();
    }

    // TODO: Check this out.
    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a)
    {
      return parameters.keySet()
          .stream()
          .map(key -> new EntriesEntry(this, key))
          .collect(Collectors.toList())
          .toArray(a);
    }

    @Override
    public boolean add(Entry<String, List<String>> entry)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
      if (!(o instanceof Entry))
      {
        return false;
      }
      Entry<?, ?> entry = (Entry) o;
      Object key = ((Entry) o).getKey();
      Object value = ((Entry) o).getValue();
      if (!(key instanceof String) || !(value instanceof List))
      {
        return false;
      }
      if (!parameters.containsKey(key) || !parameters.get(key).equals(value))
      {
        return false;
      }
      parameters.remove(key);
      return true;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
      List<EntriesEntry> entries = parameters.keySet()
          .stream()
          .map(key -> new EntriesEntry(this, key))
          .collect(Collectors.toList());
      return entries.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Entry<String, List<String>>> c)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
      return parameters.keySet()
          .stream()
          .map(key -> new EntriesEntry(this, key))
          .filter(entry -> !c.contains(entry))
          .map(EntriesEntry::getKey)
          .map(parameters::remove)
          .count() > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
      return c.stream()
          .map(this::remove)
          .reduce(false, Boolean::logicalOr);
    }

    @Override
    public void clear()
    {
      parameters.clear();
    }

    @Override
    public int hashCode()
    {
      return stream()
          .mapToInt(Entry::hashCode)
          .sum();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (!(obj instanceof Set))
      {
        return false;
      }
      Set<?> set = (Set) obj;
      return containsAll(set) && size() == set.size();
    }

    private static class EntriesIterator
        implements Iterator<Entry<String, List<String>>>
    {
      private final SyncEntries      syncEntries;
      private final Iterator<String> keys;

      private EntriesIterator(SyncEntries syncEntries)
      {
        this.syncEntries = syncEntries;
        keys = syncEntries.parameters.keySet().iterator();
      }

      @Override
      public boolean hasNext()
      {
        return keys.hasNext();
      }

      @Override
      public Entry<String, List<String>> next()
      {
        return new EntriesEntry(syncEntries, keys.next());
      }

      @Override
      public void remove()
      {
        keys.remove();
      }
    }

    @Override
    public String toString()
    {
      return "SyncEntries{" +
          parameters +
          '}';
    }

    private static class EntriesEntry
        implements Map.Entry<String, List<String>>
    {
      private final SyncEntries syncEntries;
      private final String      key;

      private EntriesEntry(SyncEntries syncEntries, String key)
      {
        this.syncEntries = syncEntries;
        this.key = key;
      }

      @Override
      public String getKey()
      {
        return key;
      }

      @Override
      public List<String> getValue()
      {
        return syncEntries.parameters.get(getKey());
      }

      @Override
      public List<String> setValue(List<String> value)
      {
        if (syncEntries.parameters.containsKey(getKey()))
        {
          return syncEntries.parameters.replace(getKey(), value);
        }
        syncEntries.parameters.put(getKey(), value);
        return null;
      }

      @Override
      public boolean equals(Object o)
      {
        if (this == o)
        {
          return true;
        }
        if (o == null || getClass() != o.getClass())
        {
          return false;
        }
        Entry<?, ?> that = (Entry) o;
        return getKey().equals(that.getKey())
            && getValue().equals(that.getValue());
      }

      @Override
      public int hashCode()
      {
        return Objects.hash(getKey(), getValue());
      }

      @Override
      public String toString()
      {
        return "EntriesEntry{" +
            "key=" + key +
            ", value='" + getValue() + '\'' +
            '}';
      }
    }
  }
}
