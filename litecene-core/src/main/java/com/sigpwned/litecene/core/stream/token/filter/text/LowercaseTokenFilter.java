package com.sigpwned.litecene.core.stream.token.filter.text;

import java.util.Locale;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.stream.token.filter.TextProcessingTokenFilter;

/**
 * Converts text tokens to their lowercase representations
 */
public class LowercaseTokenFilter extends TextProcessingTokenFilter {
  public LowercaseTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  @Override
  protected String process(String text) {
    return text.toLowerCase(Locale.US);
  }
}
