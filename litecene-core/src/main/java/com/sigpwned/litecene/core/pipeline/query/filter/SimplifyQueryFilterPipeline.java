package com.sigpwned.litecene.core.pipeline.query.filter;

import static java.util.stream.Collectors.toList;
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
        List<Query> cs = and.getChildren().stream().map(c -> simplify(c))
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
        List<Query> cs = or.getChildren().stream().map(c -> simplify(c))
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
        Query c = simplify(not.getChild());
        if (Queries.isVacuous(c))
          return VacuousQuery.INSTANCE;
        else if (c.equals(not.getChild()))
          return not;
        else
          return new NotQuery(c);
      }

      @Override
      public Query list(ListQuery list) {
        List<Query> cs = list.getChildren().stream().map(c -> simplify(c))
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
