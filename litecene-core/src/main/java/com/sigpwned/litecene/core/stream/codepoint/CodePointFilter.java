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
package com.sigpwned.litecene.core.stream.codepoint;

import com.sigpwned.litecene.core.CodePointStream;

/**
 * Modifies the upstream {@link CodePointStream} by optionally rewriting characters on a one-to-one
 * basis. Implementations must be stateless and idempotent.
 */
public abstract class CodePointFilter implements CodePointStream {
  private final CodePointStream upstream;

  public CodePointFilter(CodePointStream upstream) {
    this.upstream = upstream;
  }

  @Override
  public final int peek() {
    return filter(getUpstream().peek());
  }

  @Override
  public final int next() {
    return filter(getUpstream().next());
  }

  private CodePointStream getUpstream() {
    return upstream;
  }

  /**
   * Implements the filtering behavior. Optionally replaces the given code point, with another.
   * Filters must not map the EOF code point to any other code point, or any other code point to the
   * EOF code point.
   * 
   * To maintain the {@link #peek()} method contract, this method must be stateless, which is to say
   * that a given input must always return the same output.
   */
  protected int filter(int cp) {
    return cp;
  }
}
