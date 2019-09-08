package com.techempower.gemini.input.requestform;

public interface IBaseField<T>
  extends IField<T>
{
  /**
   * @return the type of value managed by this field
   */
  Class<T> getType();
}
