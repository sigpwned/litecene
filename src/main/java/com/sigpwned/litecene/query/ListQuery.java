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
import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Objects;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.linting.Generated;

public class ListQuery extends Query {
  private final List<Query> children;

  public ListQuery(List<Query> children) {
    if (children == null)
      throw new NullPointerException();
    if (children.isEmpty())
      throw new IllegalArgumentException("no children");
    this.children = unmodifiableList(children);
  }

  /**
   * @return the children
   */
  @Generated
  public List<Query> getChildren() {
    return children;
  }

  @Override
  public boolean isVacuous() {
    return getChildren().stream().allMatch(Query::isVacuous);
  }

  @Override
  public Query simplify() {
    List<Query> cs =
        getChildren().stream().map(Query::simplify).filter(c -> !c.isVacuous()).collect(toList());
    if (cs.size() == getChildren().size())
      return this;
    else if (cs.size() == 0)
      return VacuousQuery.INSTANCE;
    else if (cs.size() == 1)
      return cs.get(0);
    else
      return new AndQuery(cs);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(children);
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
    ListQuery other = (ListQuery) obj;
    return Objects.equals(children, other.children);
  }

  @Override
  public String toString() {
    return getChildren().stream().map(Objects::toString).collect(joining(" "));
  }
}
