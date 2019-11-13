package com.techempower.gemini.rx;

import com.techempower.gemini.Context;
import com.techempower.gemini.Dispatcher;

import javax.ws.rs.core.UriInfo;

public class JaxRsDispatcher
    implements Dispatcher
{
  public void register(Object handler)
  {

  }

  public void dispatch(String httpMethod, UriInfo uriInfo)
  {

  }

  @Override
  public boolean dispatch(Context context)
  {
    return false;
  }

  @Override
  public void dispatchComplete(Context context)
  {

  }

  @Override
  public void renderStarting(Context context, String renderingName)
  {

  }

  @Override
  public void renderComplete(Context context)
  {

  }

  @Override
  public void dispatchException(Context context,
                                Throwable exception,
                                String description)
  {

  }
}
