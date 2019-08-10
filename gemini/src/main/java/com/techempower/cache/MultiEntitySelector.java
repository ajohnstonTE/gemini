package com.techempower.cache;

import com.techempower.util.Identifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * This class is meant to help keep store accesses easy to read, by clearly
 * separating different method-value pairs into different method calls.
 */
public class MultiEntitySelector<T extends Identifiable>
{
  private final EntityStore store;
  private final Collection<Class<? extends T>> types;
  private final List<String> methods = new ArrayList<>(4);
  private final List<Object> values = new ArrayList<>(4);

  MultiEntitySelector(Collection<Class<? extends T>> types, EntityStore store)
  {
    this.types = types;
    this.store = store;
  }

  /**
   * Adds the given method-value pair to the list to use when filtering the
   * objects retrieved by {@link #list()} and {@link #get()}.
   */
  public MultiEntitySelector<T> where(String methodName, Object value)
  {
    this.methods.add(methodName);
    this.values.add(value);
    return this;
  }

  /**
   * Adds the given method-value pair to the list to use when filtering the
   * objects retrieved by {@link #list()} and {@link #get()}.
   */
  public <S> MultiEntitySelector<T> where(Function<? super T, S> method, String methodName, S value)
  {
    this.methods.add(methodName);
    this.values.add(value);
    return this;
  }

  /**
   * Adds the given method-value pair to the list to use when filtering the
   * objects retrieved by {@link #list()} and {@link #get()}.
   */
  public MultiEntitySelector<T> whereIn(String methodName, Collection<?> values)
  {
    this.methods.add(methodName);
    this.values.add(new WhereInSet(values));
    return this;
  }

  /**
   * Adds the given method-value pair to the list to use when filtering the
   * objects retrieved by {@link #list()} and {@link #get()}.
   */
  public <S> MultiEntitySelector<T> whereIn(Function<? super T, S> method, String methodName, Collection<S> values)
  {
    this.methods.add(methodName);
    this.values.add(new WhereInSet(values));
    return this;
  }

  /**
   * Returns the first entity found of the selected class whose values match
   * the specified method-value pairs.
   *
   * @see #where(String, Object)
   */
  @SuppressWarnings("unchecked")
  public T get()
  {
    if (methods.isEmpty())
    {
      for (Class<? extends T> type : types)
      {
        T object = this.store.list(type)
            .stream()
            .findFirst()
            .orElse(null);
        if (object != null)
        {
          return object;
        }
      }
      return null;
    }
    for (Class<? extends T> type : types)
    {
      T object = this.store.get(new FieldIntersection<>((Class<T>)type, methods, values));
      if (object != null)
      {
        return object;
      }
    }
    return null;
  }

  /**
   * Returns every entity of the selected class whose values match the
   * specified method-value pairs.
   *
   * @see #where(String, Object)
   */
  @SuppressWarnings("unchecked")
  public List<T> list()
  {
    List<T> toReturn = new ArrayList<>();
    if (methods.isEmpty())
    {
      for (Class<? extends T> type : types)
      {
        toReturn.addAll(this.store.list(type));
      }
    }
    else
    {
      for (Class<? extends T> type : types)
      {
        toReturn.addAll(this.store.list(
            new FieldIntersection<>((Class<T>)type, methods, values)));
      }
    }
    return toReturn;
  }
}