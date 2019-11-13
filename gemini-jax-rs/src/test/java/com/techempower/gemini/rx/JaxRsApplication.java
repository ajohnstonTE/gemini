package com.techempower.gemini.rx;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class JaxRsApplication
  extends Application
{
  private Set<Object> singletons = new HashSet<>();

  public JaxRsApplication() {
    singletons.add(new JaxRsResource());
  }

  

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
