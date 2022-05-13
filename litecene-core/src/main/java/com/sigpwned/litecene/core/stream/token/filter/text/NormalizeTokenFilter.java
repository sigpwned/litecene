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
