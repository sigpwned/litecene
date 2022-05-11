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

import java.util.Objects;
import com.sigpwned.litecene.core.linting.Generated;
import com.sigpwned.litecene.core.query.parse.token.StringToken;
import com.sigpwned.litecene.core.query.parse.token.TermToken;

public abstract class Token {
  private static class ConstantToken extends Token {
    public ConstantToken(Type type, String text) {
      super(type, text);
    }
  }

  public static final Token EOF = new ConstantToken(Type.EOF, "$");

  public static final Token LPAREN = new ConstantToken(Type.LPAREN, "(");

  public static final Token RPAREN = new ConstantToken(Type.RPAREN, ")");

  public static final Token AND = new ConstantToken(Type.AND, "AND");

  public static final Token OR = new ConstantToken(Type.OR, "OR");

  public static final Token NOT = new ConstantToken(Type.NOT, "NOT");

  public static enum Type {
    AND, OR, NOT, TERM, STRING, LPAREN, RPAREN, EOF;
  }

  private final Type type;
  private final String text;

  public Token(Type type, String text) {
    if (type == null)
      throw new NullPointerException();
    if (text == null)
      throw new NullPointerException();
    this.type = type;
    this.text = text;
  }

  public StringToken asString() {
    return (StringToken) this;
  }

  public TermToken asTerm() {
    return (TermToken) this;
  }

  /**
   * @return the type
   */
  @Generated
  public Type getType() {
    return type;
  }

  /**
   * @return the text
   */
  @Generated
  public String getText() {
    return text;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(text, type);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Token other = (Token) obj;
    return Objects.equals(text, other.text) && type == other.type;
  }

  @Override
  @Generated
  public String toString() {
    return "Token [type=" + type + ", text=" + text + "]";
  }
}
