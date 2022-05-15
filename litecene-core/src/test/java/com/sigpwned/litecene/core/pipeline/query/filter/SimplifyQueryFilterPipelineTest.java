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
package com.sigpwned.litecene.core.pipeline.query.filter;

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
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.stream.token.Tokenizer;

public class SimplifyQueryFilterPipelineTest {
  public static Query parseQuery(String s) {
    return new SimplifyQueryFilterPipeline(
        new QueryParser(new Tokenizer(new StringCodePointSource(s)))).query();
  }

  @Test
  public void shouldRemoveVacuousAndTerm() {
    Query simplifiedQuery = parseQuery("\"\" AND world");
    assertThat(simplifiedQuery,
        is(new TextQuery(asList(Term.fromString("world")), OptionalInt.empty())));
  }

  @Test
  public void shouldRemoveVacuousOrTerm() {
    Query simplifiedQuery = parseQuery("\"\" OR world");
    assertThat(simplifiedQuery,
        is(new TextQuery(asList(Term.fromString("world")), OptionalInt.empty())));
  }

  @Test
  public void shouldRemoveVacuousNotTerm() {
    Query simplifiedQuery = parseQuery("NOT \"\"");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }

  @Test
  public void shouldUnpackDoubleNotTerm() {
    Query simplifiedQuery = parseQuery("NOT NOT a");
    assertThat(simplifiedQuery,
        is(new TextQuery(asList(Term.fromString("a")), OptionalInt.empty())));
  }

  @Test
  public void shouldRemoveVacuousGroupTerm() {
    Query simplifiedQuery = parseQuery("(\"\")");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }

  @Test
  public void shouldRemoveVacuousTerm() {
    Query simplifiedQuery = parseQuery("\"\"");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }

  @Test
  public void shouldPullUpAndQuery() {
    Query simplifiedQuery = parseQuery("a AND (b AND c)");
    assertThat(simplifiedQuery,
        is(new AndQuery(asList(new TextQuery(asList(Term.fromString("a")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("b")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("c")), OptionalInt.empty())))));
  }

  @Test
  public void shouldPullUpOrQuery() {
    Query simplifiedQuery = parseQuery("a OR (b OR c)");
    assertThat(simplifiedQuery,
        is(new OrQuery(asList(new TextQuery(asList(Term.fromString("a")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("b")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("c")), OptionalInt.empty())))));
  }

  @Test
  public void shouldPullUpListQuery() {
    Query simplifiedQuery = parseQuery("a (b c)");
    assertThat(simplifiedQuery,
        is(new ListQuery(asList(new TextQuery(asList(Term.fromString("a")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("b")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("c")), OptionalInt.empty())))));
  }

  @Test
  public void shouldUnpackLonelyTerm() {
    Query simplifiedQuery = parseQuery("a b (c)");
    assertThat(simplifiedQuery,
        is(new ListQuery(asList(new TextQuery(asList(Term.fromString("a")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("b")), OptionalInt.empty()),
            new TextQuery(asList(Term.fromString("c")), OptionalInt.empty())))));
  }
}
