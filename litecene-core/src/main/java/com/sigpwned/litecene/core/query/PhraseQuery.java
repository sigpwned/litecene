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
package com.sigpwned.litecene.core.query;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.linting.Generated;

public class PhraseQuery extends Query {
  private final List<Term> terms;
  private final Integer proximity;

  public PhraseQuery(List<Term> terms, OptionalInt proximity) {
    this(terms, proximity.isPresent() ? proximity.getAsInt() : null);
  }

  public PhraseQuery(List<Term> terms, Integer proximity) {
    if (terms == null)
      throw new NullPointerException();
    this.terms = unmodifiableList(terms);
    this.proximity = proximity;
  }

  /**
   * @return the text
   */
  @Generated
  public List<Term> getTerms() {
    return terms;
  }

  /**
   * @return the proximity
   */
  public OptionalInt getProximity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(proximity, terms);
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
    PhraseQuery other = (PhraseQuery) obj;
    return Objects.equals(proximity, other.proximity) && Objects.equals(terms, other.terms);
  }

  @Override
  public String toString() {
    String result = "\"" + getTerms().stream().map(Objects::toString).collect(joining(" ")) + "\"";
    if (proximity != null)
      result = result + "~" + proximity;
    return result;
  }
}
