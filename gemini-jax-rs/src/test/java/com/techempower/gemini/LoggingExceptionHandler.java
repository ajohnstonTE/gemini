package com.techempower.gemini;

import com.esotericsoftware.minlog.Log;
import com.techempower.gemini.exceptionhandler.ExceptionHandler;
import com.techempower.log.ComponentLog;

/**
 * A default exception handler that simply logs thrown exceptions.
 */
public class LoggingExceptionHandler
    implements ExceptionHandler
{
  //
  // Member variables
  //

  private final String       COMPONENT_CODE = "logx";
  private final ComponentLog log;

  //
  // Constructor
  //

  public LoggingExceptionHandler(GeminiApplication app)
  {
    this.log = app.getLog(COMPONENT_CODE);
  }

  //
  // Member methods
  //

  @Override
  public void handleException(Context context, Throwable exc)
  {
    handleException(context, exc, null);
  }

  @Override
  public void handleException(Context context, Throwable exception, String description)
  {
    log.log("Exception thrown", Log.LEVEL_ERROR, exception);
  }

}
