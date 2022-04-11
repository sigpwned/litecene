package com.sigpwned.litecene.parse;

import java.util.Objects;

public class Token {
  public static final Token EOF = new Token(Type.EOF, "$");

  public static final Token LPAREN = new Token(Type.LPAREN, "(");

  public static final Token RPAREN = new Token(Type.RPAREN, ")");

  public static final Token AND = new Token(Type.AND, "AND");

  public static final Token OR = new Token(Type.OR, "OR");

  public static final Token NOT = new Token(Type.NOT, "NOT");

  public static Token of(Type type, String text) {
    return new Token(type, text);
  }

  public static enum Type {
    AND, OR, NOT, TERM, STRING, LPAREN, RPAREN, EOF;
  }

  private final Type type;
  private final String text;

  public Token(Type type, String text) {
    this.type = type;
    this.text = text;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, type);
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
    return Objects.equals(text, other.text) && type == other.type;
  }

  @Override
  public String toString() {
    return "Token [type=" + type + ", text=" + text + "]";
  }
}
