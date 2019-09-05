package com.techempower.gemini.input;

import com.techempower.gemini.context.CopiedQuery;
import com.techempower.gemini.context.Query;

public class SyncedInput
    extends Input
{
  private final Input       syncedInput;
  private final CopiedQuery query;

  public SyncedInput(Input syncedInput)
  {
    super(syncedInput.context());
    this.syncedInput = syncedInput;
    this.query = new CopiedQuery(syncedInput.context().getRequest(),
        syncedInput.values());
  }

  @Override
  public Query values()
  {
    return query;
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
    syncedInput.addErrorElement(element, errorMessage,
        addIfElementAlreadyPresent);

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
