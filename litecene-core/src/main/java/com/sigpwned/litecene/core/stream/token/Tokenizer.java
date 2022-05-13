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

import static java.util.stream.Collectors.toList;
import java.util.OptionalInt;
import java.util.stream.Stream;
import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.exception.EofException;
import com.sigpwned.litecene.core.exception.InvalidProximityException;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.util.Syntax;

/**
 * Implements whitespace-separated and metacharacter recognition tokenization.
 */
public class Tokenizer implements TokenStream {
  public static Tokenizer forString(String s) {
    return forCodePoints(new StringCodePointSource(s));
  }

  public static Tokenizer forCodePoints(CodePointStream stream) {
    return new Tokenizer(stream);
  }


  private final CodePointStream stream;
  private Token next;
  private StringBuilder buf;

  public Tokenizer(CodePointStream stream) {
    this.stream = stream;
    this.buf = new StringBuilder();
  }

  public Token peek() {
    if (next == null)
      next = tok();
    return next;
  }

  public Token next() {
    Token result = peek();
    next = null;
    return result;
  }

  private static final int LPAREN = Syntax.LPAREN;
  private static final int RPAREN = Syntax.RPAREN;
  private static final int QUOTE = Syntax.QUOTE;
  private static final int TILDE = Syntax.TILDE;

  private static final String AND = Syntax.AND;
  private static final String OR = Syntax.OR;
  private static final String NOT = Syntax.NOT;

  private Token tok() {
    ws();

    if (!stream.hasNext())
      return Token.EOF;

    int cp = stream.next();
    switch (cp) {
      case LPAREN:
        return Token.LPAREN;
      case RPAREN:
        return Token.RPAREN;
      case QUOTE: {
        buf.setLength(0);
        while (stream.hasNext() && stream.peek() != QUOTE) {
          buf.appendCodePoint(stream.next());
        }
        if (!stream.hasNext())
          throw new EofException();
        stream.next(); // QUOTE

        String text = buf.toString();

        Integer proximity;
        if (stream.peek() == TILDE) {
          stream.next(); // TILDE
          if (!Character.isDigit(stream.peek()))
            throw new InvalidProximityException();

          buf.setLength(0);
          do {
            buf.appendCodePoint(stream.next());
          } while (Character.isDigit(stream.peek()));

          try {
            proximity = Integer.parseInt(buf.toString());
          } catch (NumberFormatException e) {
            throw new InvalidProximityException();
          }

          if (proximity == 0)
            throw new InvalidProximityException();
        } else {
          proximity = null;
        }

        return new TextToken(
            Syntax.WHITESPACE.splitAsStream(text.strip()).map(Term::fromString).collect(toList()),
            proximity);
      }
      default: {
        buf.setLength(0);
        buf.appendCodePoint(cp);
        while (stream.hasNext() && termy(stream.peek())) {
          buf.appendCodePoint(stream.next());
        }

        String text = buf.toString();

        switch (text) {
          case AND:
            return Token.AND;
          case OR:
            return Token.OR;
          case NOT:
            return Token.NOT;
          default:
            return new TextToken(Stream.of(text.strip()).map(Term::fromString).collect(toList()),
                OptionalInt.empty());
        }
      }
    }
  }

  /**
   * Tokens are separated by whitespace or metacharacters
   */
  private boolean termy(int cp) {
    if (Character.isWhitespace(cp)) {
      // There is no version of reality where whitespace is included in terms.
      return false;
    } else {
      switch (cp) {
        case LPAREN:
        case RPAREN:
        case QUOTE:
        case TILDE:
          // These are metacharacters not allowed in a term
          return false;
        default:
          // Everything else is allowed
          return true;
      }
    }
  }

  private void ws() {
    while (stream.hasNext() && Character.isWhitespace(stream.peek()))
      stream.next();
  }
}
