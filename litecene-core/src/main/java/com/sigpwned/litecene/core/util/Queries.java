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

import static java.util.stream.Collectors.toList;
import java.util.List;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.StringQuery;
import com.sigpwned.litecene.core.query.TermQuery;
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
      public Boolean string(StringQuery string) {
        return string.getTerms().stream().allMatch(Terms::isVacuous);
      }

      @Override
      public Boolean term(TermQuery term) {
        return Terms.isVacuous(term.getText(), term.isWildcard());
      }

      @Override
      public Boolean vacuous(VacuousQuery vacuous) {
        return true;
      }
    }).process(q);
  }

  /**
   * Returns a simplified version of the given query that attempts to discard vacuous parts of the
   * query. If the entire query is vacuous, then a {@link VacuousQuery} is returned.
   */
  public static Query simplify(Query q) {
    return new QueryProcessor<Query>(new QueryProcessor.Processor<Query>() {
      @Override
      public Query and(AndQuery and) {
        List<Query> cs = and.getChildren().stream().map(Queries::simplify)
            .filter(c -> !Queries.isVacuous(c)).collect(toList());
        if (cs.size() == and.getChildren().size())
          return and;
        else if (cs.size() == 0)
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new AndQuery(cs);
      }

      @Override
      public Query or(OrQuery or) {
        List<Query> cs = or.getChildren().stream().map(Queries::simplify)
            .filter(c -> !Queries.isVacuous(c)).collect(toList());
        if (cs.size() == or.getChildren().size())
          return or;
        else if (cs.size() == 0)
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new OrQuery(cs);
      }

      @Override
      public Query not(NotQuery not) {
        Query c = Queries.simplify(not.getChild());
        if (Queries.isVacuous(c))
          return VacuousQuery.INSTANCE;
        else if (c.equals(not.getChild()))
          return not;
        else
          return new NotQuery(c);
      }

      @Override
      public Query list(ListQuery list) {
        List<Query> cs = list.getChildren().stream().map(Queries::simplify)
            .filter(c -> !Queries.isVacuous(c)).collect(toList());
        if (cs.size() == list.getChildren().size())
          return list;
        else if (cs.size() == 0)
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new ListQuery(cs);
      }

      @Override
      public Query paren(ParenQuery paren) {
        Query c = Queries.simplify(paren.getChild());
        if (Queries.isVacuous(c))
          return VacuousQuery.INSTANCE;
        else if (c instanceof ParenQuery || c instanceof StringQuery || c instanceof TermQuery
            || c instanceof NotQuery) {
          // These queries are atomic and can safely be unpacked
          return c;
        } else if (c.equals(paren.getChild()))
          return paren;
        else
          return new ParenQuery(c);
      }

      @Override
      public Query string(StringQuery string) {
        string = new StringQuery(
            string.getTerms().stream().filter(t -> !Terms.isVacuous(t)).collect(toList()),
            string.getProximity());
        if (Queries.isVacuous(string))
          return VacuousQuery.INSTANCE;
        else
          return string;
      }

      @Override
      public Query term(TermQuery term) {
        if (Queries.isVacuous(term))
          return VacuousQuery.INSTANCE;
        else
          return term;
      }

      @Override
      public Query vacuous(VacuousQuery vacuous) {
        return vacuous;
      }
    }).process(q);
  }
}
