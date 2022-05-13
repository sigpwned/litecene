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
