package com.techempower.gemini.configuration2;

abstract class Segment
{
  public abstract SegmentType getType();

  public boolean isReference()
  {
    return getType() == SegmentType.REFERENCE;
  }

  public boolean isText()
  {
    return getType() == SegmentType.TEXT;
  }
}
