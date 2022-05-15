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
package com.sigpwned.litecene.core.pipeline.query.filter;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.pipeline.query.FilterQueryPipeline;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.util.Queries;
import com.sigpwned.litecene.core.util.QueryProcessor;
import com.sigpwned.litecene.core.util.Terms;

/**
 * Eliminates vacuous query nodes
 */
public class SimplifyQueryFilterPipeline extends FilterQueryPipeline {
  public SimplifyQueryFilterPipeline(QueryPipeline upstream) {
    super(upstream);
  }

  @Override
  protected Query filter(Query query) {
    return simplify(query);
  }

  protected Query simplify(Query q) {
    return new QueryProcessor<Query>(new QueryProcessor.Processor<Query>() {
      @Override
      public Query and(AndQuery and) {
        List<Query> children = and.getChildren().stream()
            .map(SimplifyQueryFilterPipeline.this::simplify).map(c -> unpack(c, AndQuery.class))
            .filter(c -> !Queries.isVacuous(c)).collect(toList());

        List<Query> cs = new ArrayList<>();
        for (Query child : children) {
          if (child instanceof AndQuery) {
            cs.addAll(((AndQuery) child).getChildren());
          } else {
            cs.add(child);
          }
        }

        if (cs.isEmpty())
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new AndQuery(cs);
      }

      @Override
      public Query or(OrQuery or) {
        List<Query> children = or.getChildren().stream()
            .map(SimplifyQueryFilterPipeline.this::simplify).map(c -> unpack(c, OrQuery.class))
            .filter(c -> !Queries.isVacuous(c)).collect(toList());

        List<Query> cs = new ArrayList<>();
        for (Query child : children) {
          if (child instanceof OrQuery) {
            cs.addAll(((OrQuery) child).getChildren());
          } else {
            cs.add(child);
          }
        }

        if (cs.isEmpty())
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new OrQuery(cs);
      }

      @Override
      public Query list(ListQuery list) {
        List<Query> children = list.getChildren().stream()
            .map(SimplifyQueryFilterPipeline.this::simplify).map(c -> unpack(c, ListQuery.class))
            .filter(c -> !Queries.isVacuous(c)).collect(toList());

        List<Query> cs = new ArrayList<>();
        for (Query child : children) {
          if (child instanceof ListQuery) {
            cs.addAll(((ListQuery) child).getChildren());
          } else {
            cs.add(child);
          }
        }

        if (cs.isEmpty())
          return VacuousQuery.INSTANCE;
        else if (cs.size() == 1)
          return cs.get(0);
        else
          return new ListQuery(cs);
      }

      /**
       * Unpack exactly one level of parentheses around a query of a given type. This is to unpack,
       * for example, child AND queries into parent AND queries.
       */
      protected Query unpack(Query q, Class<? extends Query> c) {
        if (q instanceof ParenQuery) {
          ParenQuery pq = (ParenQuery) q;
          if (c.isInstance(pq.getChild())) {
            return pq.getChild();
          }
        }
        return q;
      }

      @Override
      public Query not(NotQuery not) {
        Query c = simplify(not.getChild());
        if (Queries.isVacuous(c))
          return VacuousQuery.INSTANCE;
        else if (c instanceof NotQuery) {
          NotQuery not2 = (NotQuery) c;
          return not2.getChild();
        } else if (c.equals(not.getChild()))
          return not;
        else
          return new NotQuery(c);
      }

      @Override
      public Query paren(ParenQuery paren) {
        Query c = simplify(paren.getChild());
        if (Queries.isVacuous(c))
          return VacuousQuery.INSTANCE;
        else if (c instanceof ParenQuery || c instanceof TextQuery || c instanceof NotQuery) {
          // These queries are atomic and can safely be unpacked
          return c;
        } else if (c.equals(paren.getChild()))
          return paren;
        else
          return new ParenQuery(c);
      }

      @Override
      public Query text(TextQuery text) {
        text = new TextQuery(
            text.getTerms().stream().filter(t -> !Terms.isVacuous(t)).collect(toList()),
            text.getProximity());
        if (Queries.isVacuous(text))
          return VacuousQuery.INSTANCE;
        else
          return text;
      }

      @Override
      public Query vacuous(VacuousQuery vacuous) {
        return vacuous;
      }
    }).process(q);
  }
}
