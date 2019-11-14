package com.techempower.gemini.rx;

import com.techempower.gemini.GeminiApplication;
import com.techempower.gemini.undertow.InfrastructureUndertow;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class FullStackDispatcherIntegrationTest
{
  @Container
  public static MySQLContainer container = new MySQLContainer();

  static class Infrastructure extends InfrastructureUndertow
  {
    List<Runnable>  runnables;
    TestApplication testApplication;

    @Override
    public GeminiApplication getApplication()
    {
      if (testApplication == null)
      {
        synchronized (this)
        {
          if (testApplication == null)
          {
            testApplication = new TestApplication();
          }
        }
      }
      return testApplication;
    }

    public void addInitTask(Runnable runnable)
    {
      if (runnables == null)
      {
        runnables = new ArrayList<>();
      }
      if (testApplication == null)
      {
        runnables.add(runnable);
      }
      else
      {
        testApplication.addInitCompleteTask(runnable);
      }
    }
  }

  static Infrastructure infrastructure;
  static HttpClient client;

  @BeforeAll
  static void setUp() throws ExecutionException, InterruptedException
  {
    infrastructure = new Infrastructure();
    CompletableFuture<?> future = new CompletableFuture<>();
    infrastructure.addInitTask(() -> future.complete(null));
    infrastructure.run(new String[]{"8080"});
    future.get();
    client = HttpClientBuilder.create()
        .build();
  }

  @AfterAll
  static void tearDown()
  {
    infrastructure.getApplication().end();
  }

  String execute(RequestBuilder requestBuilder) throws IOException
  {
    return execute(requestBuilder.build());
  }

  String execute(HttpUriRequest request) throws IOException
  {
    HttpResponse response = client.execute(request);
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(response.getEntity().getContent())))
    {
      return reader.lines().collect(Collectors.joining());
    }
  }

  @Test
  void normalGeminiHandlerWithNoParams() throws IOException
  {
    assertEquals(
        "Hello, World!",
        execute(RequestBuilder.get("http://localhost:8080/foo/bar"))
    );
  }

  @Test
  void normalGeminiHandlerWithIntegerParam() throws IOException
  {
    assertEquals(
        "Hello, World!5",
        execute(RequestBuilder.get("http://localhost:8080/foo/bar2")
            .addParameter("num", "5"))
    );
    assertEquals(
        "Hello, World!4",
        execute(RequestBuilder.get("http://localhost:8080/foo/bar2")
            .addParameter("num", "4"))
    );
  }

  @Test
  void jaxRxResourceWithNoParams() throws IOException
  {
    assertEquals(
        "Hello, World!",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar"))
    );
  }

  @Test
  void jaxRxResourceWithIntegerQueryParam() throws IOException
  {
    assertEquals(
        "Hello, World!5",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar2")
            .addParameter("num", "5"))
    );
    assertEquals(
        "Hello, World!4",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar2")
            .addParameter("num", "4"))
    );
  }

  @Test
  void jaxRxResourceWithIntegerPathParam() throws IOException
  {
    assertEquals(
        "Hello, World!7",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar3/7/more"))
    );
    assertEquals(
        "Hello, World!10",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar3/10/more"))
    );
  }

  @Test
  void jaxRxResourceWithStringPathParam() throws IOException
  {
    assertEquals(
        "Hello, World!dog",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar4/dog/more"))
    );
    assertEquals(
        "Hello, World!cat",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar4/cat/more"))
    );
  }

  @Test
  void jaxRxResourceWithJsonParam() throws IOException
  {
    assertEquals(
        "Hello, World!Person{id=4, name='Sa Mantha', age=38, abbr='SM'}",
        execute(RequestBuilder.post("http://localhost:8080/foo2/bar5")
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setEntity(new StringEntity("{\"id\":4,\"name\":\"Sa Mantha\",\"age\":38,\"abbr\":\"SM\"}")))
    );
  }

  @Test
  void jaxRxResourceReturnsJson() throws IOException
  {
    assertEquals(
        "{\"id\":7,\"name\":\"Bob Seger\",\"age\":63,\"abbr\":\"BS\"}",
        execute(RequestBuilder.get("http://localhost:8080/foo2/bar6"))
    );
  }
}
