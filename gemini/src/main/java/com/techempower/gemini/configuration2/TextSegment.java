package com.techempower.gemini.configuration2;

import java.util.Objects;

class TextSegment extends Segment
{
  private final SegmentType type;
  private final String text;

  public TextSegment(String text)
  {
    this.type = SegmentType.TEXT;
    this.text = text;
  }

  @Override
  public SegmentType getType()
  {
    return type;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof TextSegment)) return false;
    TextSegment that = (TextSegment) o;
    return getType() == that.getType()
        && Objects.equals(getText(), that.getText());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getType(), getText());
  }

  @Override
  public String toString()
  {
    return text;
  }
}
