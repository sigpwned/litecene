/*-
 * =================================LICENSE_START==================================
 * litecene-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
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
