package com.techempower.gemini.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WatchedMutableValidation
    implements MutableValidation
{
  private final MutableValidation ownValidation = new BasicMutableValidation();
  private final MutableValidation wrappedValidation;

  public WatchedMutableValidation(MutableValidation wrappedValidation)
  {
    this.wrappedValidation = wrappedValidation;
  }

  @Override
  public WatchedMutableValidation addError(String errorMessage)
  {
    ownValidation.addError(errorMessage);
    wrappedValidation.addError(errorMessage);

    return this;
  }

  @Override
  public WatchedMutableValidation addError(String element,
                                           String errorMessage,
                                           boolean addIfElementAlreadyPresent)
  {
    ownValidation.addError(element, errorMessage, addIfElementAlreadyPresent);
    wrappedValidation.addError(element, errorMessage,
        addIfElementAlreadyPresent);

    return this;
  }

  @Override
  public WatchedMutableValidation setStatusCode(int statusCode)
  {
    ownValidation.setStatusCode(statusCode);
    wrappedValidation.setStatusCode(statusCode);

    return this;
  }

  @Override
  public WatchedMutableValidation setAuxiliary(Object auxiliary)
  {
    ownValidation.setAuxiliary(auxiliary);
    wrappedValidation.setAuxiliary(auxiliary);

    return this;
  }

  @Override
  public List<String> errors()
  {
    return ownValidation.errors();
  }

  @Override
  public Map<String, Object> erroredElements()
  {
    return ownValidation.erroredElements();
  }

  @Override
  public int getStatusCode()
  {
    return ownValidation.getStatusCode();
  }

  @Override
  public Object getAuxiliary()
  {
    return ownValidation.getAuxiliary();
  }
}
