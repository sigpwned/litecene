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
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.pipeline.query.QueryParser;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.stream.token.Tokenizer;

public class QueryParserTest {
  public static Query parseQuery(String s) {
    return new QueryParser(new Tokenizer(new StringCodePointSource(s))).query();
  }

  @Test
  public void shouldParseSingleTerm() {
    Query q = parseQuery("hello");
    assertThat(q, is(new TextQuery(asList(Term.fromString("hello")), OptionalInt.empty())));
  }

  @Test
  public void shouldParseBooleanQuery() {
    Query q = parseQuery("hello OR world AND foobar");
    assertThat(q,
        is(new OrQuery(asList(new TextQuery(asList(Term.fromString("hello")), OptionalInt.empty()),
            new AndQuery(
                asList(new TextQuery(asList(Term.fromString("world")), OptionalInt.empty()),
                    new TextQuery(asList(Term.fromString("foobar")), OptionalInt.empty())))))));
  }

  @Test
  public void shouldParseProximity() {
    Query q = parseQuery("\"hello world\"~10");
    assertThat(q, is(new TextQuery(asList(Term.fromString("hello"), Term.fromString("world")),
        OptionalInt.of(10))));
  }

  @Test
  public void shouldParseTermLists() {
    Query q = parseQuery("hello world AND foobar");
    assertThat(q, is(new AndQuery(asList(
        new ListQuery(asList(new TextQuery(asList(Term.fromString("hello")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("world")), OptionalInt.empty()))),
        new TextQuery(asList(Term.fromString("foobar")), OptionalInt.empty())))));
  }
}
