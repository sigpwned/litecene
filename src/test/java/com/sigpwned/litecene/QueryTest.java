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
package com.sigpwned.litecene;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.query.ListQuery;
import com.sigpwned.litecene.query.TermQuery;
import com.sigpwned.litecene.query.parse.QueryParser;
import com.sigpwned.litecene.util.Queries;

public class QueryTest {
  @Test
  public void shouldConvertQueryWithNoDroppedCharactersToStringProperly() {
    final String input = "hello OR world AND (foobar AND NOT quux pants) AND \"alpha bravo\"~10";
    final String output = new QueryParser().query(input).toString();
    assertThat(output, is(input));
  }

  @Test
  public void shouldSimplifyVacuousAndNotProperly() {
    final String input = "hello AND NOT \"阿弥陀佛\"";
    final String output = Queries.simplify(new QueryParser().query(input)).toString();
    assertThat(output, is("hello"));
  }

  @Test
  public void shouldSimplifyVacuousOrNotProperly() {
    final String input = "hello OR NOT \"阿弥陀佛\"";
    final String output = Queries.simplify(new QueryParser().query(input)).toString();
    assertThat(output, is("hello"));
  }

  @Test
  public void shouldSimplifyDroppedTermsProperly() {
    final String input = "hello 南无阿弥陀佛 world*";
    final Query output = Queries.simplify(new QueryParser().query(input));
    assertThat(output,
        is(new ListQuery(asList(new TermQuery("hello", false), new TermQuery("world", true)))));
  }
}
