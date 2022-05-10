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
package com.sigpwned.litecene.query.parse;

import static java.util.stream.Collectors.toList;
import java.util.Locale;
import java.util.regex.Pattern;
import com.sigpwned.litecene.exception.EofException;
import com.sigpwned.litecene.exception.InvalidProximityException;
import com.sigpwned.litecene.exception.UnrecognizedCharacterException;
import com.sigpwned.litecene.query.parse.token.StringToken;
import com.sigpwned.litecene.query.parse.token.TermToken;
import com.sigpwned.litecene.util.CodePointIterator;
import com.sigpwned.litecene.util.NormalizedText;

public class QueryTokenizer {
  public static QueryTokenizer forString(String s) {
    return forNormalizedText(NormalizedText.normalize(s));
  }

  public static QueryTokenizer forNormalizedText(NormalizedText normalizedText) {
    return new QueryTokenizer(normalizedText);
  }

  private static final Pattern SPACE = Pattern.compile("[ ]+");

  private Token next;
  private CodePointIterator iterator;
  private StringBuilder buf;

  public QueryTokenizer(NormalizedText normalizedText) {
    this.iterator = CodePointIterator.forString(normalizedText.getNormalizedText());
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

  private static final int LPAREN = '(';
  private static final int RPAREN = ')';
  private static final int QUOTE = '"';
  private static final int TILDE = '~';
  private static final int STAR = '*';

  private static final String AND = "AND";
  private static final String OR = "OR";
  private static final String NOT = "NOT";

  private Token tok() {
    ws();

    if (!iterator.hasNext())
      return Token.EOF;

    int ch = iterator.next();
    switch (ch) {
      case LPAREN:
        return Token.LPAREN;
      case RPAREN:
        return Token.RPAREN;
      case QUOTE: {
        buf.setLength(0);
        while (iterator.hasNext() && iterator.peek() != QUOTE) {
          buf.appendCodePoint(transliterate(iterator.next()));
        }
        if (!iterator.hasNext())
          throw new EofException();
        if (iterator.next() != QUOTE)
          throw new AssertionError("expected quote");

        String text = buf.toString();

        Integer proximity;
        if (iterator.peek() == TILDE) {
          iterator.next(); // TILDE
          if (!Character.isDigit(iterator.peek()))
            throw new InvalidProximityException();

          buf.setLength(0);
          do {
            buf.appendCodePoint(iterator.next());
          } while (Character.isDigit(iterator.peek()));

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

        return new StringToken(SPACE.splitAsStream(text.strip()).filter(t -> !t.isEmpty())
            .map(t -> standardize(t)).collect(toList()), proximity);
      }
      default: {
        if (!termy(ch))
          throw new UnrecognizedCharacterException();

        buf.setLength(0);
        buf.appendCodePoint(transliterate(ch));
        while (iterator.hasNext() && termy(iterator.peek())) {
          buf.appendCodePoint(transliterate(iterator.next()));
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

            return new TermToken(standardize(text));
        }
      }
    }
  }

  private String standardize(String s) {
    return s.toLowerCase(Locale.ENGLISH);
  }

  private int transliterate(int ch) {
    if (ch >= 'a' && ch <= 'z') {
      return ch;
    } else if (ch >= 'A' && ch <= 'Z') {
      return ch;
    } else if (ch >= '0' && ch <= '9') {
      return ch;
    } else {
      switch (ch) {
        case STAR:
        case LPAREN:
        case RPAREN:
        case QUOTE:
        case TILDE:
          return ch;
        default:
          return ' ';
      }
    }
  }

  private boolean termy(int ch) {
    switch (ch) {
      case STAR:
        return true;
      case LPAREN:
      case RPAREN:
      case QUOTE:
      case TILDE:
        return false;
      default:
        return !Character.isWhitespace(transliterate(ch));
    }
  }

  private void ws() {
    while (iterator.hasNext() && Character.isWhitespace(transliterate(iterator.peek())))
      iterator.next();
  }
}
