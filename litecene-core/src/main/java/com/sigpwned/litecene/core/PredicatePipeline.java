package com.sigpwned.litecene.core;

/**
 * Transpiles a litecene query to another format, e.g. SQL.
 */
public interface PredicatePipeline {
  public String predicate();
}
