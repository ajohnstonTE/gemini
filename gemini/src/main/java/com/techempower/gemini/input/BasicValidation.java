package com.techempower.gemini.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class BasicValidation
  implements Validation
{
  protected int                 statusCode = 400;
  protected List<String>        errorMessages;
  protected Map<String, Object> erroredElements;
  protected Object              auxiliary;

  /**
   * Gets the list of error messages.
   */
  @JsonProperty("errors")
  @Override
  public List<String> errors()
  {
    return errorMessages;
  }

  /**
   * Gets the map of elements in error.
   */
  @JsonProperty("elements")
  @Override
  public Map<String, Object> erroredElements()
  {
    return erroredElements;
  }

  /**
   * Gets the HTTP status code.  The default is 400 ("bad request").
   */
  @JsonIgnore
  @Override
  public int getStatusCode()
  {
    return this.statusCode;
  }

  /**
   * Return the auxiliary object reference.
   */
  @JsonProperty("aux")
  @Override
  public Object getAuxiliary()
  {
    return auxiliary;
  }
}
