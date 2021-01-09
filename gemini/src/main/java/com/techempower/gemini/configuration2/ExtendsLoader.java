package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExtendsLoader
{
  private final FileOrClassPathReader sourceLoader;

  public ExtendsLoader(FileOrClassPathReader sourceLoader)
  {
    this.sourceLoader = sourceLoader;
  }

  public ObjectNode loadExtends(ObjectNode to,
                                String from) throws Exception
  {
    // No need to create a new base object here. Instead, directly write
    // loaded values to keep compatibility with the old Gemini configurator
    sourceLoader.loadFromFileOrClassPath(to, new FileSource(from));
    return to;
  }
}
