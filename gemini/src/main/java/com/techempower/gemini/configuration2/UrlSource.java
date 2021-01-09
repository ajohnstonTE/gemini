package com.techempower.gemini.configuration2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class UrlSource
{
  private final String        source;
  private       URLConnection connection;

  UrlSource(String source)
  {
    this.source = source;
  }

  public URLConnection getConnection() throws IOException
  {
    if (connection == null)
    {
      connection = new URL(source).openConnection();
    }
    return connection;
  }

  /**
   * Omits the charset/etc, if present
   */
  public String getContentType() throws IOException
  {
    String contentType = getConnection().getContentType();
    int i = contentType.indexOf(';');
    return i >= 0 ? contentType.substring(0, i) : contentType;
  }

  public String getFileExtension()
  {
    return FileSource.getFileExtension(source);
  }

  public InputStream read() throws IOException
  {
    return getConnection().getInputStream();
  }
}
