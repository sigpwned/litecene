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
