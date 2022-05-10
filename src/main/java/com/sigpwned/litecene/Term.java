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

import java.util.Locale;
import java.util.Objects;
import com.sigpwned.litecene.exception.InvalidWildcardException;
import com.sigpwned.litecene.linting.Generated;

public class Term {
  public static Term fromString(String text) {
    boolean wildcard;
    if (text.endsWith("*")) {
      text = text.substring(0, text.length() - 1);
      wildcard = true;
    } else {
      wildcard = false;
    }

    if (text.contains("*"))
      throw new InvalidWildcardException();

    return new Term(text, wildcard);
  }

  public static Term of(String text, boolean wildcard) {
    return new Term(text, wildcard);
  }

  private final String text;
  private final boolean wildcard;

  public Term(String text, boolean wildcard) {
    if (text == null)
      throw new NullPointerException();
    if (text.contains("*"))
      throw new IllegalArgumentException("text contains wildcard");
    this.text = text.toLowerCase(Locale.ENGLISH);
    this.wildcard = wildcard;
  }

  /**
   * @return the text
   */
  @Generated
  public String getText() {
    return text;
  }

  /**
   * @return the wildcard
   */
  @Generated
  public boolean isWildcard() {
    return wildcard;
  }

  public boolean isVacuous() {
    return getText().length() < 3 && !isWildcard();
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(text, wildcard);
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
    Term other = (Term) obj;
    return Objects.equals(text, other.text) && wildcard == other.wildcard;
  }

  @Override
  public String toString() {
    String result = getText();
    if (isWildcard())
      result = result + "*";
    return result;
  }
}
