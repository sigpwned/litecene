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
package com.sigpwned.litecene.core.stream.token;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.exception.EofException;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.util.TokenStreams;

public class TokenizerTest {
  @Test
  public void shouldParseAllTokenTypes() {
    Tokenizer ts =
        Tokenizer.forString("  hello world AND NOT OR ( ) 1234 \"yo dawg\" \"proxim ity\"~10  ");

    List<Token> tokens = TokenStreams.toList(ts);

    assertThat(tokens,
        is(asList(new TextToken(asList(Term.fromString("hello")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("world")), OptionalInt.empty()), Token.AND,
            Token.NOT, Token.OR, Token.LPAREN, Token.RPAREN,
            new TextToken(asList(Term.fromString("1234")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("yo"), Term.fromString("dawg")),
                OptionalInt.empty()),
            new TextToken(asList(Term.fromString("proxim"), Term.fromString("ity")),
                OptionalInt.of(10)))));

  }

  @Test
  public void shouldPerformWhitespaceTokenization() {
    Tokenizer ts =
        Tokenizer.forString(" It's a hard knock life, for us. Ît'š á härd kñōćk lïfé, fór üś! ");

    List<Token> tokens = TokenStreams.toList(ts);

    assertThat(tokens,
        is(asList(new TextToken(asList(Term.fromString("It's")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("a")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("hard")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("knock")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("life,")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("for")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("us.")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("Ît'š")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("á")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("härd")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("kñōćk")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("lïfé,")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("fór")), OptionalInt.empty()),
            new TextToken(asList(Term.fromString("üś!")), OptionalInt.empty()))));
  }

  @Test(expected = EofException.class)
  public void shouldFailToParseUnclosedString() {
    Tokenizer ts = Tokenizer.forString("\"this string is not closed.");
    for (Token t = ts.next(); t.getType() != Token.Type.EOF; t = ts.next()) {
      // Ignore...
    }
  }
}
