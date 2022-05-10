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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import com.sigpwned.litecene.query.parse.Token;

public class StringToken extends Token {
  public static class Term {
    private final String text;
    private final boolean wildcard;

    public Term(String text, boolean wildcard) {
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

    public boolean isVacuous() {
      return getText().isEmpty() && !isWildcard();
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

  private final List<Term> terms;
  private final Integer proximity;

  public StringToken(List<Term> terms, Integer proximity) {
    super(Type.STRING, terms.stream().map(Objects::toString).collect(joining(" ")));
    this.terms = unmodifiableList(terms);
    this.proximity = proximity;
  }

  /**
   * @return the terms
   */
  public List<Term> getTerms() {
    return terms;
  }

  /**
   * @return the proximity
   */
  public Integer getProximity() {
    return proximity;
  }

  public OptionalInt getProxmity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(proximity, terms);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringToken other = (StringToken) obj;
    return Objects.equals(proximity, other.proximity) && Objects.equals(terms, other.terms);
  }

  @Override
  public String toString() {
    final int maxLen = 10;
    return "StringToken [terms="
        + (terms != null ? terms.subList(0, Math.min(terms.size(), maxLen)) : null) + ", proximity="
        + proximity + "]";
  }
}
