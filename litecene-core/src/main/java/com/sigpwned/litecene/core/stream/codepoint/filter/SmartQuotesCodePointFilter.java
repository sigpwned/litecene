package com.sigpwned.litecene.core.stream.codepoint.filter;

import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.stream.codepoint.CodePointFilter;
import com.sigpwned.litecene.core.util.Syntax;

/**
 * Replaces "smart quotes" with "dumb quotes"
 */
public class SmartQuotesCodePointFilter extends CodePointFilter {
  /**
   * The double quote metacharacter in litecene syntax
   */
  public static final int QUOTATION_MARK = Syntax.QUOTE;

  /**
   * Many word processing programs, like Microsoft Word, replace opening straight quotation marks
   * (ASCII 34, ") with this character. Because the straight quote is a metacharacter, we rewrite
   * these replacements as a straight quote.
   */
  public static final int LEFT_DOUBLE_QUOTATION_MARK = '\u201C';

  /**
   * Many word processing programs, like Microsoft Word, replace closing straight quotation marks
   * (ASCII 34, ") with this character. Because the straight quote is a metacharacter, we rewrite
   * these replacements as a straight quote.
   */
  public static final int RIGHT_DOUBLE_QUOTATION_MARK = '\u201D';

  public SmartQuotesCodePointFilter(CodePointStream upstream) {
    super(upstream);
  }

  @Override
  protected int filter(int cp) {
    if (cp == LEFT_DOUBLE_QUOTATION_MARK)
      return QUOTATION_MARK;
    if (cp == RIGHT_DOUBLE_QUOTATION_MARK)
      return QUOTATION_MARK;
    return cp;
  }
}
