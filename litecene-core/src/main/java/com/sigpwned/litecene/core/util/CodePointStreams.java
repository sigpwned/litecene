/*-
 * =================================LICENSE_START==================================
 * litecene-core
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

import com.sigpwned.litecene.core.CodePointStream;

public final class CodePointStreams {
  private CodePointStreams() {}

  /**
   * Drains a {@link CodePointStream} to a {@link String}.
   */
  public static String toString(CodePointStream cps) {
    StringBuilder result = new StringBuilder();
    while (cps.hasNext())
      result.appendCodePoint(cps.next());
    return result.toString();
  }
}
