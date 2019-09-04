package com.techempower.gemini.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.techempower.helper.CollectionHelper;
import com.techempower.js.legacy.JavaScriptObject;
import com.techempower.js.legacy.VisitorFactory;
import com.techempower.js.legacy.Visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Validation
  extends JavaScriptObject
{
  /**
   * Gets the list of error messages.
   */
  @JsonProperty("errors")
  List<String> errors();

  /**
   * Gets the map of elements in error.
   */
  @JsonProperty("elements")
  Map<String, Object> erroredElements();

  /**
   * Gets the HTTP status code.  The default is 400 ("bad request").
   */
  @JsonIgnore
  int getStatusCode();

  /**
   * Are there no errors?
   */
  default boolean passed()
  {
    return (errors() == null);
  }

  /**
   * Are there any errors?
   */
  default boolean failed()
  {
    return (!passed());
  }

  /**
   * Return the auxiliary object reference.
   */
  @JsonProperty("aux")
  Object getAuxiliary();

  @Override
  default VisitorFactory<? extends Validation> getJsVisitorFactory()
  {
    return new ValidationVisitorFactory();
  }
}
