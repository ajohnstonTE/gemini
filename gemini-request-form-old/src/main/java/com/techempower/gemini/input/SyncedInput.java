package com.techempower.gemini.input;

import com.techempower.gemini.context.CopiedQuery;
import com.techempower.gemini.context.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SyncedInput
    extends Input
{
  private final Input       syncedInput;
  private final CopiedQuery query;

  public SyncedInput(Input syncedInput, boolean inheritErrors)
  {
    super(syncedInput.context());
    this.syncedInput = syncedInput;
    this.query = new CopiedQuery(syncedInput.context().getRequest(),
        syncedInput.values());
    if (inheritErrors)
    {
      inheritErrors();
    }
  }

  @SuppressWarnings("unchecked")
  protected void inheritErrors()
  {
    Optional.ofNullable(syncedInput.errors())
        .ifPresent(errors -> errors.forEach(super::addError));

    Optional.ofNullable(syncedInput.erroredElements())
        .ifPresent(erroredElements -> {
          erroredElements.forEach((name, error) -> {
            List<String> errors;
            if (error instanceof List)
            {
              errors = (List<String>) error;
            }
            else
            {
              errors = Collections.singletonList((String) error);
            }
            errors.forEach(errorMessage ->
                super.addErrorElement(name, errorMessage, true));
          });
        });
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
