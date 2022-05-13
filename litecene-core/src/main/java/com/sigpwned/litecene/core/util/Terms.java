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
package com.sigpwned.litecene.core.util;

import static java.util.stream.Collectors.toList;
import java.util.List;
import com.sigpwned.litecene.core.Term;

public final class Terms {
  private Terms() {}

  public static boolean isVacuous(Term t) {
    return isVacuous(t.getText(), t.isWildcard());
  }

  public static boolean isVacuous(String text, boolean wildcard) {
    return text.isEmpty() && !wildcard;
  }

  public static List<String> tokenize(String s) {
    return Syntax.WHITESPACE.splitAsStream(s.strip()).collect(toList());
  }

  /**
   * Returns the number of "tokens" inside this term.
   * 
   * Example: text="", wildcard=true -> 1
   * 
   * Example: text="what s up pussycat", wildcard=false -> 4
   * 
   * Example: text="what s up pussycat", wildcard=true -> 4
   */
  public static int size(Term t) {
    if (t.getText().isEmpty() && t.isWildcard()) {
      return 1;
    } else {
      return Math.toIntExact(Syntax.WHITESPACE.splitAsStream(t.getText()).count());
    }
  }
}
