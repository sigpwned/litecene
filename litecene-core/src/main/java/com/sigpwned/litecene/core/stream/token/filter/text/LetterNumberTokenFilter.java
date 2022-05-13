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
