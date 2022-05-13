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

/**
 * An ordered stream of {@link Token} with single lookahead. Generally built from a
 * {@link CodePointStream}.
 */
public interface TokenStream {
  public static Token EOF = Token.EOF;

  /**
   * Returns the current token, but does not consume it. Multiple contiguous calls to this method
   * must return the same value. A call to {@link #next()} directly following a call to this method
   * must return the same value. If the end of input has been reached, then this method must return
   * exactly {@link #EOF}.
   */
  public Token peek();

  /**
   * Returns the current token and consumes it.
   */
  public Token next();

  default boolean hasNext() {
    return peek() != Token.EOF;
  }
}
