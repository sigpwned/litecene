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
package com.sigpwned.litecene.core;

import com.sigpwned.litecene.core.query.parse.QueryParser;
import com.sigpwned.litecene.core.query.parse.QueryTokenizer;
import com.sigpwned.litecene.core.util.Queries;

public abstract class Query {
  public static Query fromString(String s) {
    NormalizedText normalized = NormalizedText.normalize(s);
    QueryTokenizer t = QueryTokenizer.forNormalizedText(normalized);
    return Queries.simplify(new QueryParser().query(t));
  }

  // NOTE: This is not the natural opposite of Query#fromString.
  @Override
  public abstract String toString();
}
