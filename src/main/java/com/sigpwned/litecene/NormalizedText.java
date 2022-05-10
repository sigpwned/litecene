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
package com.sigpwned.litecene;

import java.text.Normalizer;
import java.util.BitSet;
import java.util.Objects;
import com.sigpwned.litecene.linting.Generated;
import com.sigpwned.litecene.linting.VisibleForTesting;
import com.sigpwned.litecene.util.CodePointIterator;

/**
 * Performs unicode normalization and records what kidns of characters are removed or overwritten
 * during the process of normalization.
 * 
 * @see <a href="https://unicode.org/reports/tr15/">https://unicode.org/reports/tr15/</a>
 */
public class NormalizedText {
  private static final int NUL = '\0';

  private static final int SPACE = ' ';

  private static final int NO_TYPE = -1;

  /**
   * Convert a unicode string into a simple ASCII equivalent. The text is first normalized to
   * separate diacritics from letters, and then all non-ASCII code points are replaced by the space
   * character.
   *
   * @see Normalizer#normalize(CharSequence, java.text.Normalizer.Form)
   */
  public static NormalizedText normalize(String originalText) {
    // Normalize our string. We use compatible decomposition.
    // See: https://unicode.org/reports/tr15/#Norm_Forms
    String nfkd = Normalizer.normalize(originalText, Normalizer.Form.NFKD);

    // Process our normalized string
    BitSet removedTypes = new BitSet();
    StringBuilder normalizedText = new StringBuilder();
    CodePointIterator iterator = CodePointIterator.forString(nfkd);
    while (iterator.hasNext()) {
      int cp = iterator.next();

      int type = -1;
      int representation;
      if (cp >= 0x20 && cp <= 0x7E) {
        // This is a good old fashioned ASCII characters. Add.
        representation = cp;
      } else {
        // These are non-ASCII characters. We will either ignore them entirely or add a space where
        // they used to be, depending on what kind of character they are.
        type = Character.getType(cp);
        switch (type) {
          case Character.NON_SPACING_MARK:
          case Character.COMBINING_SPACING_MARK:
          case Character.ENCLOSING_MARK:
            // These are the diacritics we're trying to remove. We should ignore them. If we add a
            // space for these, then words containing letters with diacritics will get split by the
            // space we would otherwise add. Example: We want "fůňķŷ" -> "funky", not "f u n k y".
            representation = NUL;
            break;
          default:
            // This is everything else. We should add a space.
            representation = SPACE;
            break;
        }
      }

      // Did we remove this character? If so, record that.
      if (representation != cp) {
        if (type == NO_TYPE)
          type = Character.getType(cp);
        removedTypes.set(type);
      }

      // Are we discarding this character? If not, add it.
      if (representation != NUL) {
        normalizedText.appendCodePoint(representation);
      }
    }

    return new NormalizedText(originalText, normalizedText.toString(), removedTypes);
  }

  private final String originalText;
  private final String normalizedText;
  private final BitSet removedTypes;

  @Generated
  @VisibleForTesting
  NormalizedText(String originalText, String normalizedText, BitSet removedTypes) {
    this.originalText = originalText;
    this.normalizedText = normalizedText;
    this.removedTypes = removedTypes;
  }

  /**
   * @return the originalText
   */
  @Generated
  public String getOriginalText() {
    return originalText;
  }

  /**
   * @return the normalizedText
   */
  @Generated
  public String getNormalizedText() {
    return normalizedText;
  }

  /**
   * @return the removedTypes
   */
  @Generated
  public BitSet getRemovedTypes() {
    return removedTypes;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(normalizedText, originalText, removedTypes);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NormalizedText other = (NormalizedText) obj;
    return Objects.equals(normalizedText, other.normalizedText)
        && Objects.equals(originalText, other.originalText)
        && Objects.equals(removedTypes, other.removedTypes);
  }

  @Override
  @Generated
  public String toString() {
    return "Normalization [originalText=" + originalText + ", normalizedText=" + normalizedText
        + ", removedTypes=" + removedTypes + "]";
  }
}
