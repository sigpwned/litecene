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
package com.sigpwned.litecene.core.query.token;

import java.util.Objects;
import java.util.OptionalInt;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.linting.Generated;

public class PhraseToken extends Token {
  private final Integer proximity;

  @Generated
  public PhraseToken(String text, OptionalInt proximity) {
    this(text, proximity.isPresent() ? proximity.getAsInt() : null);
  }

  @Generated
  public PhraseToken(String text, Integer proximity) {
    super(Type.PHRASE, text);
    this.proximity = proximity;
  }

  public OptionalInt getProximity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(proximity);
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
    PhraseToken other = (PhraseToken) obj;
    return Objects.equals(proximity, other.proximity);
  }

  @Override
  @Generated
  public String toString() {
    return "PhraseToken [proximity=" + proximity + ", getText()=" + getText() + "]";
  }
}
