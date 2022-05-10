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
package com.sigpwned.litecene.query.parse.token;

import java.util.Objects;
import com.sigpwned.litecene.Term;
import com.sigpwned.litecene.linting.Generated;
import com.sigpwned.litecene.query.parse.Token;

public class TermToken extends Token {
  private final boolean wildcard;

  public TermToken(Term term) {
    this(term.getText(), term.isWildcard());
  }

  public TermToken(String text, boolean wildcard) {
    super(Type.TERM, text);
    this.wildcard = wildcard;
  }

  /**
   * @return the wildcard
   */
  @Generated
  public boolean isWildcard() {
    return wildcard;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(wildcard);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TermToken other = (TermToken) obj;
    return wildcard == other.wildcard;
  }

  @Override
  @Generated
  public String toString() {
    return "TermToken [wildcard=" + wildcard + "]";
  }
}
