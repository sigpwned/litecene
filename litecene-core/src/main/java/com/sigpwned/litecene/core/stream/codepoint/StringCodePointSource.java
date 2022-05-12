package com.sigpwned.litecene.core.stream.codepoint;

import com.sigpwned.litecene.core.CodePointStream;

/**
 * Creates a stream of code points from the given string.
 */
public class StringCodePointSource implements CodePointStream {
  private final String text;
  private int index;

  public StringCodePointSource(String text) {
    this.text = text;
    this.index = 0;
  }

  @Override
  public int peek() {
    if (index >= text.length())
      return EOF;
    else
      return text.codePointAt(index);
  }

  @Override
  public int next() {
    int result = peek();
    if (result != EOF)
      index = index + Character.charCount(result);
    return result;
  }
}
