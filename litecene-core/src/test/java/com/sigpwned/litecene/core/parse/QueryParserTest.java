/*-
 * =================================LICENSE_START==================================
 * litecene
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
package com.sigpwned.litecene.core.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.TermQuery;

public class QueryParserTest {
  @Test
  public void shouldParseSingleTerm() {
    Query q = Query.fromString("hello");
    assertThat(q, is(new TermQuery("hello", false)));
  }

  @Test
  public void shouldParseBooleanQuery() {
    Query q = Query.fromString("hello OR world AND foobar");
    assertThat(q, is(new OrQuery(asList(new TermQuery("hello", false),
        new AndQuery(asList(new TermQuery("world", false), new TermQuery("foobar", false)))))));
  }

  @Test
  public void shouldParseTermLists() {
    Query q = Query.fromString("hello world AND foobar");
    assertThat(q,
        is(new AndQuery(asList(
            new ListQuery(asList(new TermQuery("hello", false), new TermQuery("world", false))),
            new TermQuery("foobar", false)))));
  }

  @Test
  public void shouldTolerateDroppedCharacters() {
    Query q = Query.fromString("hello's 南无阿弥陀佛 world AND foobar");
    assertThat(q,
        is(new AndQuery(
            asList(new ListQuery(asList(new TermQuery("hello", false), new TermQuery("s", false),
                new TermQuery("world", false))), new TermQuery("foobar", false)))));
  }
}
