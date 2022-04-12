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

import com.sigpwned.litecene.exception.EofException;
import com.sigpwned.litecene.exception.InvalidProximityException;
import com.sigpwned.litecene.exception.UnrecognizedCharacterException;
import com.sigpwned.litecene.query.parse.token.StringToken;
import com.sigpwned.litecene.query.parse.token.TermToken;

public class QueryTokenizer {
  public static QueryTokenizer forString(String s) {
    return new QueryTokenizer(s);
  }

  private Token next;
  private CodePointIterator iterator;
  private StringBuilder buf;

  public QueryTokenizer(String s) {
    this.iterator = CodePointIterator.forString(s);
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
        } else {
          proximity = null;
        }

        return new StringToken(text, proximity);
      }
      default: {
        if (!termy(iterator.peek()))
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

  private boolean termy(int ch) {
    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'))
      return true;
    if (ch == LPAREN || ch == RPAREN || ch == QUOTE)
      return false;
    switch (Character.getType(ch)) {
      // Letter ///////////////////////////////////////////////////////////////
      case Character.LOWERCASE_LETTER: // Ll
      case Character.MODIFIER_LETTER: // Lm
      case Character.OTHER_LETTER: // Lo
      case Character.TITLECASE_LETTER: // Lt
      case Character.UPPERCASE_LETTER: // Lu
        return true;

      // Mark /////////////////////////////////////////////////////////////////
      case Character.COMBINING_SPACING_MARK: // Mc
      case Character.ENCLOSING_MARK: // Me
      case Character.NON_SPACING_MARK: // Mn
        return true;

      // Number ///////////////////////////////////////////////////////////////
      case Character.DECIMAL_DIGIT_NUMBER: // Nd
      case Character.LETTER_NUMBER: // Nl
      case Character.OTHER_NUMBER: // No
        return true;

      // Punctuation //////////////////////////////////////////////////////////
      case Character.CONNECTOR_PUNCTUATION: // Pc
      case Character.DASH_PUNCTUATION: // Pd
      case Character.END_PUNCTUATION: // Pe
      case Character.FINAL_QUOTE_PUNCTUATION: // Pf
      case Character.INITIAL_QUOTE_PUNCTUATION: // Pi
      case Character.OTHER_PUNCTUATION: // Po
      case Character.START_PUNCTUATION: // Ps
        return true;

      // Control //////////////////////////////////////////////////////////////
      case Character.CONTROL: // Cc
      case Character.FORMAT: // Cf
      case Character.UNASSIGNED: // Cn
      case Character.SURROGATE: // Cs
        return false;

      // Symbol ///////////////////////////////////////////////////////////////
      case Character.CURRENCY_SYMBOL: // Sc
      case Character.MODIFIER_SYMBOL: // Sk
      case Character.MATH_SYMBOL: // Sm
      case Character.OTHER_SYMBOL: // So
        return true;

      // Separator ////////////////////////////////////////////////////////////
      case Character.LINE_SEPARATOR: // Zl
      case Character.PARAGRAPH_SEPARATOR: // Zp
      case Character.SPACE_SEPARATOR: // Zs
        return false;

      // I have no idea. //////////////////////////////////////////////////////
      default:
        return false;
    }
  }

  private void ws() {
    while (Character.isWhitespace(iterator.peek()))
      iterator.next();
  }
}
