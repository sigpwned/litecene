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
package com.sigpwned.litecene.core.stream.token;

import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;

/**
 * Modifies the upstream {@link TokenStream} by optionally rewriting tokens on a one-to-one basis.
 * Implementations must be stateless and idempotent.
 */
public abstract class TokenFilter implements TokenStream {
  private final TokenStream upstream;

  protected TokenFilter(TokenStream upstream) {
    this.upstream = upstream;
  }

  @Override
  public final Token peek() {
    return filter(getUpstream().peek());
  }

  @Override
  public final Token next() {
    return filter(getUpstream().next());
  }

  /**
   * Implements the filtering behavior. Optionally replaces the given token with another. Filters
   * must not map the EOF token to any other code point, or any other token to the EOF code point.
   * 
   * To maintain the {@link #peek()} method contract, this method must be stateless, which is to say
   * that a given input must always return the same output.
   */
  protected Token filter(Token token) {
    return token;
  }

  /**
   * @return the upstream
   */
  private TokenStream getUpstream() {
    return upstream;
  }
}
