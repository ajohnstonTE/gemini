package com.techempower.gemini.rx;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

class JaxRxDispatchTest
{
  void testDispatch()
  {
    JaxRsDispatcher dispatcher = new JaxRsDispatcher();
    dispatcher.register(new ExampleResource());
    //dispatcher.dispatch(HttpMethod.GET, new BasicUriInfo("test/example"));
    //dispatcher.dispatch(HttpMethod.GET, new BasicUriInfo("test/example2"));
    // TODO: Support query params, etc.
  }

  @Path("example")
  public static class ExampleResource
  {
    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String doTest()
    {
      return "Hello, World!";
    }

    @Path("test2")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String doTest2(@QueryParam("num") Integer num)
    {
      return "Hello, World! num: " + num;
    }
  }
}
