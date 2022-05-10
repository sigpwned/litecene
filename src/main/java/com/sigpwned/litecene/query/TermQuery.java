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
package com.sigpwned.litecene.query;

import java.util.Objects;
import com.sigpwned.litecene.Query;

public class TermQuery extends Query {
  private final String text;
  private final boolean wildcard;

  public TermQuery(String text, boolean wildcard) {
    this.text = text;
    this.wildcard = wildcard;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the wildcard
   */
  public boolean isWildcard() {
    return wildcard;
  }

  @Override
  public boolean isVacuous() {
    return getText().length() < 3 && !isWildcard();
  }

  @Override
  public Query simplify() {
    return isVacuous() ? VacuousQuery.INSTANCE : this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, wildcard);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TermQuery other = (TermQuery) obj;
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
