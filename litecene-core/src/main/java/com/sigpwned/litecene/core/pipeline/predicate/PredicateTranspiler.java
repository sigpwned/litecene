package com.sigpwned.litecene.core.pipeline.predicate;

import com.sigpwned.litecene.core.PredicatePipeline;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.QueryTranspiler;

public class PredicateTranspiler implements PredicatePipeline {
  private final QueryPipeline query;
  private final QueryTranspiler transpiler;

  public PredicateTranspiler(QueryPipeline query, QueryTranspiler transpiler) {
    this.query = query;
    this.transpiler = transpiler;
  }

  @Override
  public String predicate() {
    return getTranspiler().transpile(getQuery().query());
  }

  /**
   * @return the query
   */
  private QueryPipeline getQuery() {
    return query;
  }

  /**
   * @return the transpiler
   */
  private QueryTranspiler getTranspiler() {
    return transpiler;
  }
}
