package com.techempower.gemini.configuration2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileSource
{
  private final String source;

  public FileSource(String source)
  {
    this.source = source;
  }

  public String getFileExtension()
  {
    return getFileExtension(source);
  }

  public InputStream readFromSystemOrClassPath() throws IOException
  {
    InputStream inputStream = FileSource.class.getResourceAsStream(source);
    if (inputStream == null)
    {
      inputStream = ClassLoader.getSystemResourceAsStream(source);
    }
    if (inputStream == null)
    {
      Path path = Paths.get(source);
      if (Files.exists(path))
      {
        inputStream = Files.newInputStream(path);
      }
    }
    return inputStream;
  }

  public static String getFileExtension(String source)
  {
    int i = source.lastIndexOf('.');
    return i >= 0 && i + 1 < source.length() ? source.substring(i + 1) : "";
  }
}
