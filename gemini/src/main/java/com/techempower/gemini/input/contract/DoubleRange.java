package com.techempower.gemini.input.contract;

import com.techempower.helper.NumberHelper;
import com.techempower.util.Args;
import com.techempower.util.IntRange;

import java.util.Iterator;

/**
 * A minimum and maximum double, together defining a range.
 */
public class DoubleRange
    implements Iterable<Double>
{
  
  //
  // Some standard ranges.
  //
  
  public static final DoubleRange POSITIVE         = new DoubleRange(1, Double.MAX_VALUE);
  public static final DoubleRange POSITIVE_OR_ZERO = new DoubleRange(0, Double.MAX_VALUE);
  public static final DoubleRange NEGATIVE         = new DoubleRange(Double.MIN_VALUE, -1);
  public static final DoubleRange NEGATIVE_OR_ZERO = new DoubleRange(Double.MIN_VALUE, 0);
  
  public final double min;
  public final double max;
  
  /**
   * Bound a double argument by enforcing a minimum. Adapted from {@link Args#intMin(int, String, int)}
   */
  private static void doubleMin(double argument, String name, double minimum)
  {
    if (argument < minimum)
    {
      throw new IllegalArgumentException(
          name + " must be at least " + minimum + ".");
    }
  }
  
  /**
   * Constructor.
   */
  public DoubleRange(double min, double max)
  {
    doubleMin(max, "max", min);
    this.min = min;
    this.max = max;
  }
  
  @Override
  public Iterator<Double> iterator()
  {
    return new Iterator<Double>()
    {
      double position = min;
      
      @Override
      public void remove()
      {
      }
      
      @Override
      public Double next()
      {
        return position++;
      }
      
      @Override
      public boolean hasNext()
      {
        return position <= max;
      }
    };
  }
  
  /**
   * Return the full extent that the range covers from end to end; the width
   * of the range as depicted on a number line.  For example, the width of
   * the range (0,2) is 2 and the width of (-1,1) is also 2.
   *
   * <pre>
   * new DoubleRange(4,5).span(); // 1
   * </pre>
   */
  public double width()
  {
    return Math.abs(max - min);
  }
  
  /**
   * Return the number of doubles contained in this range, inclusive of both
   * edges.  In other words, the count is the width plus 1.  For example, the
   * count of (0,2) is 3 because the range encompasses three doubles: 0, 1,
   * and 2.
   *
   * <pre>
   * new DoubleRange(4,5).count(); // 2
   * </pre>
   */
  public double count()
  {
    return this.width() + 1;
  }
  
  /**
   * Is a provided number in the range?
   */
  public boolean contains(double number)
  {
    return ((number >= min) && (number <= max));
  }
  
  /**
   * Bound a provided parameter by this range, meaning that inputs outside
   * of the range will be trimmed to the edge of the range.  An input
   * exceeding the maximum will be set to the maximum; an input lower than
   * the minimum will be set to the minimum.
   */
  public double bound(double number)
  {
    return boundDouble(number, this);
  }
  
  
  /**
   * Force a provided long to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum. Adapted from
   * {@link NumberHelper#boundInteger(int, IntRange)}
   */
  private static double boundDouble(double toBound, DoubleRange range)
  {
    return boundDouble(toBound, range.min, range.max);
  }
  
  /**
   * Force a provided long to be bounded by a minimum and maximum.  If the
   * number is lower than the minimum, it will be set to the minimum; if it
   * is higher than the maximum, it will be set to the maximum. Adapted from
   * * {@link NumberHelper#boundInteger(int, int, int)}
   */
  private static double boundDouble(double toBound, double minimum, double maximum)
  {
    double result = toBound;
    if (result < minimum)
    {
      result = minimum;
    }
    if (result > maximum)
    {
      result = maximum;
    }
    
    return result;
  }
}
