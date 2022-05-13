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

import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;

public final class Queries {
  private Queries() {}

  /**
   * Returns true if the given query is vacuous, which is to say has no meaningful standard for
   * filtering text. Example: a list query with no queries in it
   */
  public static boolean isVacuous(Query q) {
    return new QueryProcessor<Boolean>(new QueryProcessor.Processor<Boolean>() {
      @Override
      public Boolean and(AndQuery and) {
        return and.getChildren().stream().allMatch(Queries::isVacuous);
      }

      @Override
      public Boolean or(OrQuery or) {
        return or.getChildren().stream().allMatch(Queries::isVacuous);
      }

      @Override
      public Boolean not(NotQuery not) {
        return isVacuous(not.getChild());
      }

      @Override
      public Boolean list(ListQuery list) {
        return list.getChildren().stream().allMatch(Queries::isVacuous);
      }

      @Override
      public Boolean paren(ParenQuery paren) {
        return isVacuous(paren.getChild());
      }

      @Override
      public Boolean text(TextQuery text) {
        return text.getTerms().stream().allMatch(Terms::isVacuous);
      }

      @Override
      public Boolean vacuous(VacuousQuery vacuous) {
        return true;
      }
    }).process(q);
  }
}
