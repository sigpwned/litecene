package com.sigpwned.litecene.core;

/**
 * Converts a litecene query into another syntax, e.g. SQL
 */
public interface QueryTranspiler {
  public String transpile(Query query);
}
