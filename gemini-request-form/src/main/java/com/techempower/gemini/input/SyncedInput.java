package com.techempower.gemini.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncedInput
    extends Input
{
  private final Input syncedInput;

  public SyncedInput(Input syncedInput)
  {
    super(syncedInput.context());
    if (true) throw new UnsupportedOperationException(
        "Query still needs to be \"isolated\"/synced/copied");
    this.syncedInput = syncedInput;
  }

  @Override
  public SyncedInput addError(String errorMessage)
  {
    super.addError(errorMessage);
    syncedInput.addError(errorMessage);

    return this;
  }

  @Override
  public SyncedInput addError(String element,
                              String errorMessage,
                              boolean addIfElementAlreadyPresent)
  {
    super.addError(element, errorMessage, addIfElementAlreadyPresent);
    syncedInput.addError(element, errorMessage, addIfElementAlreadyPresent);

    return this;
  }

  @Override
  public SyncedInput setStatusCode(int statusCode)
  {
    super.setStatusCode(statusCode);
    syncedInput.setStatusCode(statusCode);

    return this;
  }

  @Override
  public SyncedInput setAuxiliary(Object auxiliary)
  {
    super.setAuxiliary(auxiliary);
    syncedInput.setAuxiliary(auxiliary);

    return this;
  }
}
