package com.sigpwned.litecene.core.stream.token.filter.text;

import java.util.regex.Pattern;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.stream.token.filter.TextProcessingTokenFilter;

/**
 * Retains all code points between 0x20 and 0x7E and converts all other code points to the ASCII
 * space character (dec 32). Strips whitespace from beginning and end of result and replaces runs of
 * whitespace with a single space.
 */
public class PrintableAsciiTokenFilter extends TextProcessingTokenFilter {
  public PrintableAsciiTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  private static final Pattern NON_PRINTABLE_ASCII = Pattern.compile("[^\\u0020-\\u007E]+");

  @Override
  protected String process(String text) {
    return NON_PRINTABLE_ASCII.matcher(text).replaceAll(" ").strip();
  }
}
