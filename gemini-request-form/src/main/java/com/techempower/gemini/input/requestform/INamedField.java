package com.techempower.gemini.input.requestform;

public interface INamedField<T>
    extends IField<T>
{
  /**
   * @return the name of the field
   */
  String getName();
}
