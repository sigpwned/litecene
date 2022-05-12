package com.sigpwned.litecene.core.stream.token;

import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;

/**
 * Modifies the upstream {@link TokenStream} by optionally rewriting tokens on a one-to-one basis.
 * Implementations must be stateless and idempotent.
 */
public abstract class TokenFilter implements TokenStream {
  private final TokenStream upstream;

  public TokenFilter(TokenStream upstream) {
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
