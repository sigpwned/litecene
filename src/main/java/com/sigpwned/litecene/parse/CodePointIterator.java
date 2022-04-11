package com.sigpwned.litecene.parse;

public class CodePointIterator {
  public static final int EOF = -1;

  public static CodePointIterator forString(String text) {
    return new CodePointIterator(text);
  }

  private final String text;
  private int index;

  public CodePointIterator(String text) {
    if (text == null)
      throw new NullPointerException();
    this.text = text;
    this.index = 0;
  }

  public boolean hasNext() {
    return index < getText().length();
  }

  public int peek() {
    return hasNext() ? getText().codePointAt(index) : -1;
  }

  public int next() {
    int result = peek();
    if (result != EOF)
      index += Character.charCount(result);
    return result;
  }

  private String getText() {
    return text;
  }
}
