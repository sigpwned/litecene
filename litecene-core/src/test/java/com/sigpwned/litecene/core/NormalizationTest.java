/*-
 * =================================LICENSE_START==================================
 * litecene
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
package com.sigpwned.litecene.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.BitSet;
import org.junit.Test;

public class NormalizationTest {
  @Test
  public void shouldRetainDecoratedLatinCharacters() {
    final String originalText = "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ";
    assertThat(NormalizedText.normalize(originalText), is(new NormalizedText(originalText,
        "This is a funky String", bitSetOf(Character.NON_SPACING_MARK))));
  }

  @Test
  public void shouldDiscardNonLatinLetters() {
    final String originalText = "Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ Æ Ø Ð ß";
    assertThat(NormalizedText.normalize(originalText),
        is(new NormalizedText(originalText, "This is a funky String        ", bitSetOf(
            Character.UPPERCASE_LETTER, Character.LOWERCASE_LETTER, Character.NON_SPACING_MARK))));
  }

  @Test
  public void shouldDiscardAsianLetters() {
    final String originalText = "南无阿弥陀佛";
    assertThat(NormalizedText.normalize(originalText),
        is(new NormalizedText(originalText, "      ", bitSetOf(Character.OTHER_LETTER))));
  }

  private static BitSet bitSetOf(int... bits) {
    BitSet result = new BitSet();
    for (int bit : bits)
      result.set(bit);
    return result;
  }
}
