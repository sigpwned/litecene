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
