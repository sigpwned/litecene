package com.sigpwned.litecene.core.pipeline.query;

import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;

/**
 * Performs a query rewrite. Query rewrites can be arbitrary and complex but should generally retain
 * query semantics.
 */
public abstract class FilterQueryPipeline implements QueryPipeline {
  private final QueryPipeline upstream;

  public FilterQueryPipeline(QueryPipeline upstream) {
    this.upstream = upstream;
  }

  @Override
  public final Query query() {
    return filter(getUpstream().query());
  }

  /**
   * @return the upstream
   */
  private QueryPipeline getUpstream() {
    return upstream;
  }

  protected abstract Query filter(Query query);
}
