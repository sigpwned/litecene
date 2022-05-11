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
package com.sigpwned.litecene.core.query.parse;

import com.sigpwned.litecene.core.NormalizedText;
import com.sigpwned.litecene.core.exception.EofException;
import com.sigpwned.litecene.core.exception.InvalidProximityException;
import com.sigpwned.litecene.core.exception.UnrecognizedCharacterException;
import com.sigpwned.litecene.core.query.parse.token.PhraseToken;
import com.sigpwned.litecene.core.query.parse.token.TermToken;
import com.sigpwned.litecene.core.util.CodePointIterator;

public class QueryTokenizer {
  public static QueryTokenizer forString(String s) {
    return forNormalizedText(NormalizedText.normalize(s));
  }

  public static QueryTokenizer forNormalizedText(NormalizedText normalizedText) {
    return new QueryTokenizer(normalizedText);
  }

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
          buf.appendCodePoint(iterator.next());
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

        return new PhraseToken(text, proximity);
      }
      default: {
        if (!termy(ch))
          throw new UnrecognizedCharacterException();

        buf.setLength(0);
        buf.appendCodePoint(ch);
        while (iterator.hasNext() && termy(iterator.peek())) {
          buf.appendCodePoint(iterator.next());
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
            return new TermToken(text);
        }
      }
    }
  }

  /**
   * We only search for content that's alphanumerical. This method recognizes content that's
   * relevant to searching content. In particular, metacharacters are ignored.
   */
  private boolean termy(int cp) {
    if (cp >= 0x7F) {
      // 0x7F is DELETE. 0x80 and above is not 7-bit ASCII.
      return false;
    }
    if (Character.isWhitespace(cp)) {
      // There is no version of reality where whitespace is included in terms.
      return false;
    }
    if (cp <= 0x20) {
      // Everything 0x20 and below that isn't whitespace is control characters, which are not
      // interesting.
      return false;
    }
    switch (cp) {
      case LPAREN:
      case RPAREN:
      case QUOTE:
      case TILDE:
        // These are metacharacters not allowed in a term
        return false;
      case STAR:
      default:
        // Everything else is allowed
        return true;
    }
  }

  private void ws() {
    while (iterator.hasNext() && Character.isWhitespace(iterator.peek()))
      iterator.next();
  }
}
