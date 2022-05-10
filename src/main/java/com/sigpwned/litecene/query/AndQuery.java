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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.linting.Generated;

public class AndQuery extends Query {
  private final List<Query> children;

  public AndQuery(List<Query> children) {
    if (children.size() < 2)
      throw new IllegalArgumentException("not enough children");
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
    AndQuery other = (AndQuery) obj;
    return Objects.equals(children, other.children);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    Iterator<Query> iterator = getChildren().iterator();
    result.append(iterator.next().toString());
    while (iterator.hasNext()) {
      result.append(" AND ");
      result.append(iterator.next().toString());
    }

    return result.toString();
  }
}
