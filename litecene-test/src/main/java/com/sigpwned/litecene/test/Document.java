/*-
 * =================================LICENSE_START==================================
 * litecene-test
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
package com.sigpwned.litecene.test;

import java.util.Objects;

public class Document {
  public static Document of(String id, String text) {
    return new Document(id, text);
  }

  private final String id;
  private final String text;

  public Document(String id, String text) {
    this.id = id;
    this.text = text;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Document other = (Document) obj;
    return Objects.equals(id, other.id) && Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return "Document [id=" + id + ", text=" + text + "]";
  }
}
