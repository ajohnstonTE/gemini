package com.techempower.gemini.rx;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("foo2")
public class JaxRsResource
{
  @Path("bar")
  @GET
  public String handleBar()
  {
    return "Hello, World!";
  }

  @Path("bar2")
  @GET
  public String handleBar2(@QueryParam("num") int num)
  {
    return "Hello, World!" + num;
  }

  @Path("bar3/{someId}/more")
  @GET
  public String handleBar3(@PathParam("someId") int someId)
  {
    return "Hello, World!" + someId;
  }

  @Path("bar4/{animal}/more")
  @GET
  public String handleBar4(@PathParam("animal") String theAnimal)
  {
    return "Hello, World!" + theAnimal;
  }
}
