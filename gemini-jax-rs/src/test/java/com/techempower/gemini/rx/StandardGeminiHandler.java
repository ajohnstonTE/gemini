package com.techempower.gemini.rx;

import com.techempower.gemini.Context;
import com.techempower.gemini.GeminiApplication;
import com.techempower.gemini.path.MethodSegmentHandler;
import com.techempower.gemini.path.annotation.Get;
import com.techempower.gemini.path.annotation.PathSegment;

public class StandardGeminiHandler
    extends MethodSegmentHandler<Context>
{
  public StandardGeminiHandler(GeminiApplication app)
  {
    super(app);
  }

  @PathSegment("bar")
  @Get
  public boolean handleBar()
  {
    return text("Hello, World!");
  }

  @PathSegment("bar2")
  @Get
  public boolean handleBar2()
  {
    return text("Hello, World!" + query().getInt("num"));
  }
}
