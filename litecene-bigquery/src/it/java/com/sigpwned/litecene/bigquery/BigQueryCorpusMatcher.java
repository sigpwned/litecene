/*-
 * =================================LICENSE_START==================================
 * litecene-bigquery
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
package com.sigpwned.litecene.bigquery;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Set;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.common.collect.Streams;
import com.sigpwned.litecene.bigquery.util.BigQuerySearching;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.test.Corpus;
import com.sigpwned.litecene.test.CorpusMatcher;

public class BigQueryCorpusMatcher implements CorpusMatcher {
  @Override
  public Set<String> match(Corpus corpus, Query query) throws IOException {
    String sql = String.format(
        "WITH corpus AS (%s), analyzed AS (SELECT id, text, %s AS analyzed FROM corpus) SELECT DISTINCT id FROM analyzed a WHERE (%s)",
        corpus.getDocuments().stream()
            .map(d -> String.format("SELECT %s AS id, %s AS text", emitString(d.getId()),
                emitString(d.getText())))
            .collect(joining(" UNION ALL ")),
        BigQuerySearching.recommendedAnalysisExpr("text"),
        new BigQuerySearchCompiler("a.analyzed", true).compile(query));

    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sql).build();

    TableResult results;
    try {
      results = bigquery.query(queryConfig);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException();
    }

    return Streams.stream(results.iterateAll()).map(row -> row.get("id").getStringValue())
        .collect(toSet());
  }

  private String emitString(String s) {
    return "'" + s.strip().replace("'", "\\'") + "'";
  }
}
