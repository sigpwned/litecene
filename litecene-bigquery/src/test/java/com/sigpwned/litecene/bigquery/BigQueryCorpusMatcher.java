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
import java.io.IOException;
import java.util.Set;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.test.Corpus;
import com.sigpwned.litecene.test.CorpusMatcher;

public class BigQueryCorpusMatcher implements CorpusMatcher {
  @Override
  public Set<String> match(Corpus corpus, Query query) throws IOException {
    String sql = String.format("WITH corpus AS (%s) SELECT DISTINCT id FROM corpus c WHERE (%s)",
        corpus.getDocuments().stream()
            .map(d -> String.format("SELECT %s AS id, %s AS text", emitString(d.getId()),
                emitString(d.getText())))
            .collect(joining(" UNION ALL ")),
        new BigQuerySearchCompiler("c.text", true).compile(query));

    // TODO Run query

    throw new UnsupportedOperationException();
  }

  private String emitString(String s) {
    return "'" + s.strip().replace("'", "\\'") + "'";
  }
}
