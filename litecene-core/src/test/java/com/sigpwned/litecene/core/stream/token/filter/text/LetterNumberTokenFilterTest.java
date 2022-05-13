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
package com.sigpwned.litecene.core.stream.token.filter.text;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.stream.token.ListTokenSource;
import com.sigpwned.litecene.core.util.TokenStreams;

public class LetterNumberTokenFilterTest {
  @Test
  public void shouldReplaceNonPrintableAsciiInTextTokens() {
    TokenStream source = new ListTokenSource(
        asList(new TextToken(asList(Term.fromString("füñkÿ123#z")), OptionalInt.empty())));

    TokenStream ts = new LetterNumberTokenFilter(source);

    List<Token> tokens = TokenStreams.toList(ts);

    assertThat(tokens,
        is(asList(new TextToken(asList(Term.fromString("füñkÿ123 z")), OptionalInt.empty()))));
  }

  @Test
  public void shouldNotTouchNonTextTokens() {
    TokenStream source = new ListTokenSource(asList(Token.AND, Token.LPAREN, Token.OR, Token.NOT,
        Token.RPAREN, new TextToken(asList(Term.fromString("füñkÿ123#z")), OptionalInt.empty())));

    TokenStream ts = new LetterNumberTokenFilter(source);

    List<Token> tokens = TokenStreams.toList(ts);

    assertThat(tokens, is(asList(Token.AND, Token.LPAREN, Token.OR, Token.NOT, Token.RPAREN,
        new TextToken(asList(Term.fromString("füñkÿ123 z")), OptionalInt.empty()))));
  }

  @Test
  public void shouldAdjustProximityWhenTermTokenCountChanges() {
    TokenStream source = new ListTokenSource(asList(new TextToken(
        asList(Term.fromString("füñkÿ123#z"), Term.fromString("hello")), OptionalInt.of(10))));

    TokenStream ts = new LetterNumberTokenFilter(source);

    List<Token> tokens = TokenStreams.toList(ts);

    assertThat(tokens,
        is(asList(new TextToken(asList(Term.fromString("füñkÿ123 z"), Term.fromString("hello")),
            OptionalInt.of(11)))));
  }
}
