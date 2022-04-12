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
import java.util.OptionalInt;
import com.sigpwned.litecene.Query;

public class StringQuery extends Query {
  private final String text;
  private final Integer proximity;

  public StringQuery(String text, Integer proximity) {
    this.text = text;
    this.proximity = proximity;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the proximity
   */
  public OptionalInt getProximity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  @Override
  public int hashCode() {
    return Objects.hash(proximity, text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringQuery other = (StringQuery) obj;
    return Objects.equals(proximity, other.proximity) && Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder().append("\"").append(getText()).append("\"");
    if (proximity != null) {
      result.append("~").append(proximity);
    }
    return result.toString();
  }
}
