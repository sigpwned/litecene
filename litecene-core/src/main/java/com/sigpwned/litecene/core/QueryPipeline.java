package com.sigpwned.litecene.core;

/**
 * Produces a single {@link Query} non-repeatably. Generally built on top of a {@link TokenStream}.
 */
public interface QueryPipeline {
  /**
   * Produces one {@link Query} non-repeatably.
   */
  public Query query();
}
