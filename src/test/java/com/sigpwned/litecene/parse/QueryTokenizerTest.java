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
package com.sigpwned.litecene.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.sigpwned.litecene.exception.EofException;
import com.sigpwned.litecene.query.parse.QueryTokenizer;
import com.sigpwned.litecene.query.parse.Token;
import com.sigpwned.litecene.query.parse.token.StringToken;
import com.sigpwned.litecene.query.parse.token.TermToken;

public class QueryTokenizerTest {
  @Test
  public void shouldParseAllTokenTypes() {
    QueryTokenizer ts = QueryTokenizer
        .forString("  hello world AND NOT OR ( ) 1234 \"yo dawg\" \"proxim ity\"~10  ");

    List<Token> tokens = new ArrayList<>();
    do {
      tokens.add(ts.next());
    } while (tokens.get(tokens.size() - 1).getType() != Token.Type.EOF);

    assertThat(tokens, is(asList(new TermToken("hello", false), new TermToken("world", false),
        Token.AND, Token.NOT, Token.OR, Token.LPAREN, Token.RPAREN, new TermToken("1234", false),
        new StringToken(
            asList(new StringToken.Term("yo", false), new StringToken.Term("dawg", false)), null),
        new StringToken(
            asList(new StringToken.Term("proxim", false), new StringToken.Term("ity", false)), 10),
        Token.EOF)));

  }

  @Test
  public void shouldSplitOnNonAlphanumCharacters() {
    QueryTokenizer ts = QueryTokenizer
        .forString(" It's a hard knock life, for us. Ît’š á härd kñōćk lïfé, fór üś! ");

    List<Token> tokens = new ArrayList<>();
    do {
      tokens.add(ts.next());
    } while (tokens.get(tokens.size() - 1).getType() != Token.Type.EOF);

    assertThat(tokens, is(asList(new TermToken("it", false), new TermToken("s", false),
        new TermToken("a", false), new TermToken("hard", false), new TermToken("knock", false),
        new TermToken("life", false), new TermToken("for", false), new TermToken("us", false),
        new TermToken("it", false), new TermToken("s", false), new TermToken("a", false),
        new TermToken("hard", false), new TermToken("knock", false), new TermToken("life", false),
        new TermToken("for", false), new TermToken("us", false), Token.EOF)));

  }

  @Test(expected = EofException.class)
  public void shouldFailToParseUnclosedString() {
    QueryTokenizer ts = QueryTokenizer.forString("\"this string is not closed.");
    for (Token t = ts.next(); t.getType() != Token.Type.EOF; t = ts.next()) {
      // Ignore...
    }
  }
}
