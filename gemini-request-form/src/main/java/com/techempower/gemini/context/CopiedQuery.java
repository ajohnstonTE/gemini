package com.techempower.gemini.context;

import com.techempower.gemini.Request;

import java.util.HashMap;

public class CopiedQuery
  extends Query
{
  public CopiedQuery(Request request, Query queryToCopy)
  {
    super(request);
    if (queryToCopy.override != null)
    {
      this.override = new HashMap<>(queryToCopy.override);
    }
  }
}
