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
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.exception.EofException;
import com.sigpwned.litecene.core.query.parse.QueryTokenizer;
import com.sigpwned.litecene.core.query.parse.Token;
import com.sigpwned.litecene.core.query.parse.token.PhraseToken;
import com.sigpwned.litecene.core.query.parse.token.TermToken;

public class QueryTokenizerTest {
  @Test
  public void shouldParseAllTokenTypes() {
    QueryTokenizer ts = QueryTokenizer
        .forString("  hello world AND NOT OR ( ) 1234 \"yo dawg\" \"proxim ity\"~10  ");

    List<Token> tokens = new ArrayList<>();
    do {
      tokens.add(ts.next());
    } while (tokens.get(tokens.size() - 1).getType() != Token.Type.EOF);

    assertThat(tokens,
        is(asList(new TermToken("hello"), new TermToken("world"), Token.AND, Token.NOT, Token.OR,
            Token.LPAREN, Token.RPAREN, new TermToken("1234"),
            new PhraseToken("yo dawg", OptionalInt.empty()), new PhraseToken("proxim ity", 10),
            Token.EOF)));

  }

  @Test
  public void shouldPerformWhitespaceTokenization() {
    QueryTokenizer ts = QueryTokenizer
        .forString(" It's a hard knock life, for us. Ît'š á härd kñōćk lïfé, fór üś! ");

    List<Token> tokens = new ArrayList<>();
    do {
      tokens.add(ts.next());
    } while (tokens.get(tokens.size() - 1).getType() != Token.Type.EOF);

    assertThat(tokens, is(asList(new TermToken("It's"), new TermToken("a"), new TermToken("hard"),
        new TermToken("knock"), new TermToken("life,"), new TermToken("for"), new TermToken("us."),
        new TermToken("It's"), new TermToken("a"), new TermToken("hard"), new TermToken("knock"),
        new TermToken("life,"), new TermToken("for"), new TermToken("us!"), Token.EOF)));

  }

  @Test(expected = EofException.class)
  public void shouldFailToParseUnclosedString() {
    QueryTokenizer ts = QueryTokenizer.forString("\"this string is not closed.");
    for (Token t = ts.next(); t.getType() != Token.Type.EOF; t = ts.next()) {
      // Ignore...
    }
  }
}
