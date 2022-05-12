package com.sigpwned.litecene.core;

import java.io.IOException;

/**
 * A text stream abstraction that allows reading code points one at a time with single lookahead.
 * Note that the reading methods do not throw an {@link IOException}, so the text being read is
 * assumed to fit in memory.
 * 
 * For a description of code points, see
 * <a href="https://docs.oracle.com/javase/tutorial/i18n/text/characterClass.html">the
 * documentation</a>.
 */
public interface CodePointStream {
  public static final int EOF = -1;

  /**
   * Returns the current code point, but does not consume it. Multiple contiguous calls to this
   * method must return the same value. A call to {@link #next()} directly following a call to this
   * method must return the same value. If the end of input has been reached, then this method must
   * return exactly {@link #EOF}.
   */
  public int peek();

  /**
   * Returns the current code point and consumes it.
   */
  public int next();

  /**
   * Returns true if there is at least one more code point in the stream, or false otherwise.
   */
  default boolean hasNext() {
    return peek() != EOF;
  }
}
