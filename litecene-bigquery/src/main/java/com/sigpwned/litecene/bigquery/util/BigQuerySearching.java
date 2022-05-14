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
package com.sigpwned.litecene.bigquery.util;

import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.pipeline.query.QueryParser;
import com.sigpwned.litecene.core.pipeline.query.filter.SimplifyQueryFilterPipeline;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.stream.codepoint.filter.SmartQuotesCodePointFilter;
import com.sigpwned.litecene.core.stream.token.Tokenizer;
import com.sigpwned.litecene.core.stream.token.filter.text.LetterNumberTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.LowercaseTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.NormalizeTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.PrintableAsciiTokenFilter;

public final class BigQuerySearching {
  private BigQuerySearching() {}

  /**
   * Matches recommended BigQuery analysis expression
   * 
   * @see #recommendedAnalysisExpr(String)
   */
  public static QueryPipeline recommendedQueryPipeline(String q) {
    return new SimplifyQueryFilterPipeline(new QueryParser(new LowercaseTokenFilter(
        new LetterNumberTokenFilter(new PrintableAsciiTokenFilter(new NormalizeTokenFilter(
            new Tokenizer(new SmartQuotesCodePointFilter(new StringCodePointSource(q)))))))));
  }

  /**
   * Parses a query using the recommended query pipeline
   * 
   * @see #recommendedQueryPipeline(String)
   */
  public static Query recommendedParseQuery(String q) {
    return recommendedQueryPipeline(q).query();
  }

  /**
   * Performs a reasonable analysis of text using BigQuery SQL
   */
  public static String recommendedAnalysisExpr(String field) {
    return String.format(
        "LOWER(TRIM(REGEXP_REPLACE(REGEXP_REPLACE(NORMALIZE(%s, NFKD), r\"\\p{M}\", ''), r\"[^a-zA-Z0-9]+\", ' ')))",
        field);
  }
}
