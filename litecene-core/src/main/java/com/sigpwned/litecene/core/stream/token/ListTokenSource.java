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
package com.sigpwned.litecene.core.stream.token;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.ListIterator;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;

/**
 * Returns a fixed list of {@link Token} objects. Useful for testing.
 */
public class ListTokenSource implements TokenStream {
  private final ListIterator<Token> iterator;

  public ListTokenSource(List<Token> tokens) {
    this.iterator = unmodifiableList(tokens).listIterator();
  }

  @Override
  public Token peek() {
    if (getIterator().hasNext()) {
      Token result = getIterator().next();
      getIterator().previous();
      return result;
    } else {
      return Token.EOF;
    }
  }

  @Override
  public Token next() {
    if (getIterator().hasNext()) {
      return getIterator().next();
    } else {
      return Token.EOF;
    }
  }

  /**
   * @return the iterator
   */
  private ListIterator<Token> getIterator() {
    return iterator;
  }
}
