package com.techempower.gemini.configuration2;

import java.util.Objects;

class ReferenceSegment extends Segment
{
  private final SegmentType type;
  private final String path;
  private final String dollarSignsPrefix;

  public ReferenceSegment(String dollarSignsPrefix,
                          String path)
  {
    this.type = SegmentType.REFERENCE;
    this.path = path;
    this.dollarSignsPrefix = dollarSignsPrefix;
  }

  @Override
  public SegmentType getType()
  {
    return type;
  }

  public String getPath()
  {
    return path;
  }

  public String getDollarSignsPrefix()
  {
    return dollarSignsPrefix;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof ReferenceSegment)) return false;
    ReferenceSegment that = (ReferenceSegment) o;
    return getType() == that.getType()
        && Objects.equals(getPath(), that.getPath())
        && Objects.equals(getDollarSignsPrefix(), that.getDollarSignsPrefix());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getType(), getPath(), getDollarSignsPrefix());
  }

  @Override
  public String toString()
  {
    String dollarSignsPrefix = this.dollarSignsPrefix != null
        ? this.dollarSignsPrefix : "";
    return dollarSignsPrefix + "${{" + path + "}}";
  }
}
