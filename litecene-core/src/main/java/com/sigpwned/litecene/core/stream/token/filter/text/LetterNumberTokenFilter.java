package com.sigpwned.litecene.core.stream.token.filter.text;

import java.util.regex.Pattern;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.stream.token.filter.TextProcessingTokenFilter;

/**
 * Re-tokenizes text tokens to use (unicode) letters and numbers only. This can be combined with the
 * {@link NormalizeTokenFilter} to create an "ASCII alnum" filter.
 */
public class LetterNumberTokenFilter extends TextProcessingTokenFilter {
  public LetterNumberTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  private static final Pattern NON_ALNUM = Pattern.compile("[^\\p{L}\\p{N}]+");

  @Override
  protected String process(String text) {
    return NON_ALNUM.matcher(text).replaceAll(" ").strip();
  }
}
