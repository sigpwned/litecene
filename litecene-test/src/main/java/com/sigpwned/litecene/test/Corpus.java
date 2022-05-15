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

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;
import com.sigpwned.litecene.core.linting.Generated;

public class Corpus {
  public static Corpus of(List<Document> documents) {
    return new Corpus(documents);
  }

  private final List<Document> documents;

  public Corpus(List<Document> documents) {
    this.documents = unmodifiableList(documents);
  }

  /**
   * @return the documents
   */
  @Generated
  public List<Document> getDocuments() {
    return documents;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(documents);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Corpus other = (Corpus) obj;
    return Objects.equals(documents, other.documents);
  }

  @Override
  @Generated
  public String toString() {
    final int maxLen = 10;
    return "Corpus [documents="
        + (documents != null ? documents.subList(0, Math.min(documents.size(), maxLen)) : null)
        + "]";
  }
}
