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
import com.sigpwned.litecene.core.linting.Generated;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.StringQuery;
import com.sigpwned.litecene.core.query.TermQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;

public class QueryProcessor<T> {
  public static interface Processor<T> {
    default T and(AndQuery and) {
      return null;
    }

    default T or(OrQuery or) {
      return null;
    }

    default T not(NotQuery not) {
      return null;
    }

    default T list(ListQuery list) {
      return null;
    }

    default T paren(ParenQuery paren) {
      return null;
    }

    default T string(StringQuery string) {
      return null;
    }

    default T term(TermQuery term) {
      return null;
    }

    default T vacuous(VacuousQuery vacuous) {
      return null;
    }
  }

  private final Processor<T> handler;

  public QueryProcessor(Processor<T> handler) {
    if (handler == null)
      throw new NullPointerException();
    this.handler = handler;
  }

  public T process(Query q) {
    if (q instanceof AndQuery) {
      AndQuery and = (AndQuery) q;
      return getHandler().and(and);
    } else if (q instanceof OrQuery) {
      OrQuery or = (OrQuery) q;
      return getHandler().or(or);
    } else if (q instanceof NotQuery) {
      NotQuery not = (NotQuery) q;
      return getHandler().not(not);
    } else if (q instanceof ListQuery) {
      ListQuery list = (ListQuery) q;
      return getHandler().list(list);
    } else if (q instanceof ParenQuery) {
      ParenQuery paren = (ParenQuery) q;
      return getHandler().paren(paren);
    } else if (q instanceof StringQuery) {
      StringQuery string = (StringQuery) q;
      return getHandler().string(string);
    } else if (q instanceof TermQuery) {
      TermQuery term = (TermQuery) q;
      return getHandler().term(term);
    } else if (q instanceof VacuousQuery) {
      VacuousQuery vacuous = (VacuousQuery) q;
      return getHandler().vacuous(vacuous);
    } else {
      throw new AssertionError("unrecognized query " + q);
    }
  }

  /**
   * @return the handler
   */
  @Generated
  private Processor<T> getHandler() {
    return handler;
  }
}
