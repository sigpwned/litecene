package com.sigpwned.litecene.core.stream.token.filter.text;

import java.text.Normalizer;
import java.util.regex.Pattern;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.stream.token.filter.TextProcessingTokenFilter;

/**
 * Replaces text tokens with "simplified" latin representations. For example, replace "fůňķŷ" with
 * "funky".
 */
public class NormalizeTokenFilter extends TextProcessingTokenFilter {
  public NormalizeTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  /**
   * Recognizes a single character in the Unicode mark category
   */
  private static final Pattern MARK = Pattern.compile("\\p{M}");


  @Override
  protected String process(String text) {
    return MARK.matcher(Normalizer.normalize(text, Normalizer.Form.NFKD)).replaceAll("");
  }
}
