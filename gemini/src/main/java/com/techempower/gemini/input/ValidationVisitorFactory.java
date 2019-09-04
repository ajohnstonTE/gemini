package com.techempower.gemini.input;

import com.techempower.js.legacy.Visitor;
import com.techempower.js.legacy.VisitorFactory;
import com.techempower.js.legacy.Visitors;

class ValidationVisitorFactory
    implements VisitorFactory<Validation>
{
  @Override
  public Visitor visitor(Validation result)
  {
    return Visitors.map(
        "errors", result.errors(),
        "elements", result.erroredElements(),
        "aux", result.getAuxiliary()
    );
  }
}
