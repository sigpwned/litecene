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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.bigquery.util.BigQuerySearching;

/**
 * Some day, we should do actual unit testing with zetasql. However, zetasql does not support Java
 * on M1 Macs right now. So let's settle for just some smoke tests.
 */
public class BigQuerySmokeTest {
  @Test
  public void shouldCompileComplexQuery() {
    String sql = new BigQuerySearchCompiler("t.text", false).compile(BigQuerySearching
        .recommendedParseQuery("hello OR (world AND \"foo bar*\" AND \"alpha bravo\"~4)"));
    assertThat(sql, containsString("t.text"));
  }
}
