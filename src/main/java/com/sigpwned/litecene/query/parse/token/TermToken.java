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

import com.sigpwned.litecene.query.parse.Token;

public class TermToken extends Token {
  private final boolean wildcard;

  public TermToken(String text, boolean wildcard) {
    super(Type.TERM, text);
    this.wildcard = wildcard;
  }

  /**
   * @return the wildcard
   */
  public boolean isWildcard() {
    return wildcard;
  }
}
