package com.techempower.gemini.configuration2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VariableReferenceParser
{
  // A semi-complicated regex that should allow simple processing of references.
  private static final Pattern REFERENCE = Pattern.compile("(?<!\\$)(?<skip>\\$)?(?<escaped>(?:\\$\\$)*)\\$\\{(?<path>[^${}\\s]+)}");

  public List<Segment> parse(String str)
  {
    List<Segment> segments = new ArrayList<>();
    Matcher matcher = REFERENCE.matcher(str);
    int start;
    int end = 0;
    while (matcher.find())
    {
      start = matcher.start();
      if (start != end)
      {
        segments.add(new TextSegment(str.substring(end, start)));
      }
      end = matcher.end();
      String skip = matcher.group("skip");
      String escaped = matcher.group("escaped");
      String path = matcher.group("path");
      boolean isSkipped = skip != null && !skip.isEmpty();
      if (isSkipped)
      {
        escaped += "$$";
        segments.add(new TextSegment(escaped + "{" + path + "}"));
      }
      else
      {
        segments.add(new ReferenceSegment(escaped, path));
      }
    }
    if (end != str.length())
    {
      segments.add(new TextSegment(str.substring(end)));
    }
    return segments;
  }
}
