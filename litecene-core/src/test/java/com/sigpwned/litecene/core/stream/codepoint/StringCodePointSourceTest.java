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
package com.sigpwned.litecene.core.stream.codepoint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.util.CodePointStreams;

public class StringCodePointSourceTest {
  @Test
  public void shouldReturnAllCodePointsInOrder() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    String output = CodePointStreams.toString(cps);

    assertThat(output, is(input));
    assertThat(cps.peek(), is(CodePointStream.EOF));
  }

  @Test
  public void twoPeeksShouldReturnSameValue() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    int peek1 = cps.peek();
    int peek2 = cps.peek();

    assertThat(peek2, is(peek1));
  }

  @Test
  public void twoNextsShouldReturnDifferentValue() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    int next1 = cps.next();
    int next2 = cps.next();

    assertThat(next2, not(is(next1)));
  }
}
