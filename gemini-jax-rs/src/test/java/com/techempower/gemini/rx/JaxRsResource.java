package com.techempower.gemini.rx;

import com.techempower.gemini.params.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

  @Path("bar5")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @POST
  public String handleBar5(Person param)
  {
    return "Hello, World!" + param;
  }

  @Path("bar6")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public Person handleBar6()
  {
    return new Person()
        .setId(7)
        .setName("Bob Seger")
        .setAge(63)
        .setAbbr("BS");
  }
}
