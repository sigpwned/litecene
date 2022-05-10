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
package com.sigpwned.litecene.util;

public class CodePointIterator {
  public static final int EOF = -1;

  public static CodePointIterator forString(String text) {
    return new CodePointIterator(text);
  }

  private final String text;
  private int index;

  public CodePointIterator(String text) {
    if (text == null)
      throw new NullPointerException();
    this.text = text;
    this.index = 0;
  }

  public boolean hasNext() {
    return index < getText().length();
  }

  public int peek() {
    return hasNext() ? getText().codePointAt(index) : EOF;
  }

  public int next() {
    int result = peek();
    if (result != EOF)
      index += Character.charCount(result);
    return result;
  }

  private String getText() {
    return text;
  }
}
