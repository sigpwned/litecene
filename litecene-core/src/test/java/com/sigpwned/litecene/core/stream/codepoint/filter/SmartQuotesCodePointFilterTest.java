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
package com.sigpwned.litecene.core.stream.codepoint.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;

public class SmartQuotesCodePointFilterTest {
  @Test
  public void shouldReplaceSmartquotes() {
    CodePointStream cps =
        new SmartQuotesCodePointFilter(new StringCodePointSource("\u201Chello world\u201D"));

    StringBuilder buf = new StringBuilder();
    while (cps.hasNext())
      buf.appendCodePoint(cps.next());

    assertThat(buf.toString(), is("\"hello world\""));
  }
}
