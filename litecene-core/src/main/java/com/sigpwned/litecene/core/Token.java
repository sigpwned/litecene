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
package com.sigpwned.litecene.core;

import java.util.Objects;
import com.sigpwned.litecene.core.linting.Generated;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.util.Syntax;

public abstract class Token {
  private static class ConstantToken extends Token {
    private final String text;

    public ConstantToken(Type type, String text) {
      super(type);
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  /**
   * Indicates the end of a {@link TokenStream}.
   */
  public static final Token EOF = new ConstantToken(Type.EOF, "$");

  /**
   * Opens a query group
   */
  public static final Token LPAREN =
      new ConstantToken(Type.LPAREN, new String(new int[] {Syntax.LPAREN}, 0, 1));

  /**
   * Closes a query group
   */
  public static final Token RPAREN =
      new ConstantToken(Type.RPAREN, new String(new int[] {Syntax.RPAREN}, 0, 1));


  /**
   * Boolean "and" operator
   */
  public static final Token AND = new ConstantToken(Type.AND, Syntax.AND);

  /**
   * Boolean "or" operator
   */
  public static final Token OR = new ConstantToken(Type.OR, Syntax.OR);

  /**
   * Boolean "not" operator
   */
  public static final Token NOT = new ConstantToken(Type.NOT, Syntax.NOT);

  public static enum Type {
    AND, OR, NOT, TEXT, LPAREN, RPAREN, EOF;
  }

  private final Type type;

  public Token(Type type) {
    if (type == null)
      throw new NullPointerException();
    this.type = type;
  }

  public TextToken asText() {
    return (TextToken) this;
  }

  /**
   * @return the type
   */
  @Generated
  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Token other = (Token) obj;
    return type == other.type;
  }

  public abstract String toString();
}
