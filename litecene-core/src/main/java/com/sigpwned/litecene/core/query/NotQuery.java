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

import java.util.Objects;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.linting.Generated;

public class NotQuery extends Query {
  private final Query child;

  public NotQuery(Query child) {
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
    NotQuery other = (NotQuery) obj;
    return Objects.equals(child, other.child);
  }

  @Override
  @Generated
  public String toString() {
    return "NOT " + getChild();
  }
}
