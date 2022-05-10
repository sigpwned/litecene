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
import com.sigpwned.litecene.linting.Generated;

public class ParenQuery extends Query {
  private final Query child;

  public ParenQuery(Query child) {
    if (child == null)
      throw new NullPointerException();
    this.child = child;
  }

  /**
   * @return the child
   */
  @Generated
  public Query getChild() {
    return child;
  }

  @Override
  public boolean isVacuous() {
    return getChild().isVacuous();
  }

  @Override
  public Query simplify() {
    Query c = getChild().simplify();
    if (c.isVacuous())
      return VacuousQuery.INSTANCE;
    else if (c instanceof ParenQuery || c instanceof StringQuery || c instanceof TermQuery
        || c instanceof NotQuery) {
      // These queries are atomic and can safely be unpacked
      return c;
    } else if (c.equals(child))
      return this;
    else
      return new NotQuery(c);
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(child);
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
    ParenQuery other = (ParenQuery) obj;
    return Objects.equals(child, other.child);
  }

  @Override
  public String toString() {
    return "(" + getChild().toString() + ")";
  }
}
