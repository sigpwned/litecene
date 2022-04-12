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

public class NotQuery extends Query {
  private final Query child;

  public NotQuery(Query child) {
    this.child = child;
  }

  /**
   * @return the child
   */
  public Query getChild() {
    return child;
  }

  @Override
  public int hashCode() {
    return Objects.hash(child);
  }

  @Override
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
  public String toString() {
    return "NOT " + getChild().toString();
  }
}
