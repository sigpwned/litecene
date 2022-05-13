/*-
 * =================================LICENSE_START==================================
 * litecene-core
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
