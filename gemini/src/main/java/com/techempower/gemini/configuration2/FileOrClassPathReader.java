package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface FileOrClassPathReader
{
  ObjectNode loadFromFileOrClassPath(ObjectNode node,
                                     FileSource source)
      throws Exception;
}
