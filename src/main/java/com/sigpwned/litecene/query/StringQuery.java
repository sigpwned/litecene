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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.Term;
import com.sigpwned.litecene.linting.Generated;

public class StringQuery extends Query {
  private final List<Term> terms;
  private final Integer proximity;

  public StringQuery(List<Term> terms, Integer proximity) {
    if (terms == null)
      throw new NullPointerException();
    if (terms.isEmpty())
      throw new IllegalArgumentException("no terms");
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
  public boolean isVacuous() {
    return getTerms().stream().allMatch(Term::isVacuous);
  }

  @Override
  public Query simplify() {
    return isVacuous() ? VacuousQuery.INSTANCE : this;
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
    StringQuery other = (StringQuery) obj;
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
