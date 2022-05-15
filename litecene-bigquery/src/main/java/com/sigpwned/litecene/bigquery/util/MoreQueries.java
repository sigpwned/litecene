/*-
 * =================================LICENSE_START==================================
 * litecene-bigquery
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
package com.sigpwned.litecene.bigquery.util;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import java.util.HashSet;
import java.util.Set;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.util.Queries;
import com.sigpwned.litecene.core.util.QueryProcessor;

public final class MoreQueries {
  private MoreQueries() {}

  /**
   * BigQuery has limited full-text search capabilities, but they can already handle some queries
   * completely. This method returns true if the given query can be handled entirely by the BigQuery
   * SEARCH function, and false otherwise.
   */
  public static boolean isFullySearchable(Query q) {
    return new QueryProcessor<Boolean>(new QueryProcessor.Processor<Boolean>() {
      /**
       * For an and query, we are completely searchable if all our children are.
       * 
       * Example: hello world AND hello you -> true
       * 
       * Example: hell* world AND hello you -> false
       */
      @Override
      public Boolean and(AndQuery and) {
        return and.getChildren().stream().allMatch(MoreQueries::isFullySearchable);
      }

      /**
       * There are some corner cases where or queries are fully searchable, but they are pretty
       * niche. Let's just say or queries are never completely searchable.
       */
      @Override
      public Boolean or(OrQuery or) {
        return false;
      }

      /**
       * Because we do not track required tokens for not queries, they are not fully searchable.
       */
      @Override
      public Boolean not(NotQuery not) {
        return false;
      }

      /**
       * For a list query, we are completely searchable if all our children are.
       * 
       * Example: hello world AND hello you -> true
       * 
       * Example: hell* world AND hello you -> false
       */
      @Override
      public Boolean list(ListQuery list) {
        return list.getChildren().stream().allMatch(MoreQueries::isFullySearchable);
      }

      /**
       * For a paren query, we are fully searchable if our child is.
       * 
       * Exmaple: (hello) -> true
       */
      @Override
      public Boolean paren(ParenQuery paren) {
        return isFullySearchable(paren.getChild());
      }

      /**
       * For a string query, we are fully searchable if we have exactly one non-wildcard token. The
       * SEARCH function does not consider relative ordering of tokens.
       * 
       * Exmaple: "hello" -> true
       * 
       * Example: "hell*" -> false
       * 
       * Example: "hello world" -> false
       */
      @Override
      public Boolean text(TextQuery text) {
        return text.getTerms().size() == 1 && !text.getTerms().get(0).isWildcard();
      }

      /**
       * For a vacuous query, there are no required terms, by definition.
       */
      @Override
      public Boolean vacuous(VacuousQuery vacuous) {
        return false;
      }
    }).process(q);
  }

  /**
   * Calculates the tokens that must be present in a document for the given query to match. The
   * returned set should be considered a lower bound and not exact.
   * 
   * @param q The query to analyze. Must be simplified.
   * 
   * @see Queries#simplify(Query)
   */
  public static Set<String> requiredTokens(Query q) {
    return new QueryProcessor<Set<String>>(new QueryProcessor.Processor<Set<String>>() {
      /**
       * For an and query, we require the union of all required terms of our children
       * 
       * Exmaple: hello world AND hello you -> [ hello, world, you ]
       */
      @Override
      public Set<String> and(AndQuery and) {
        return and.getChildren().stream().flatMap(c -> requiredTokens(c).stream()).collect(toSet());
      }

      /**
       * For an or query, we require the intersection of all required terms of our children
       * 
       * Exmaple: hello world OR hello you -> [ hello ]
       */
      @Override
      public Set<String> or(OrQuery or) {
        Set<String> result = new HashSet<>(requiredTokens(or.getChildren().get(0)));
        for (int i = 1; i < or.getChildren().size(); i++) {
          result.retainAll(requiredTokens(or.getChildren().get(i)));
          if (result.isEmpty()) {
            // If we ever get empty, then we're done. We'll always be empty.
            break;
          }
        }

        return result;
      }

      /**
       * There is some complex boolean logic we could do to track NOT query required terms, but for
       * now, just say there are no required terms.
       */
      @Override
      public Set<String> not(NotQuery not) {
        // If we hit a "not", then just bail out on tracking for now.
        return emptySet();
      }

      /**
       * For a list query, we require the union of all required terms of our children
       * 
       * Exmaple: hell* world -> [ world ]
       */
      @Override
      public Set<String> list(ListQuery list) {
        return list.getChildren().stream().flatMap(c -> requiredTokens(c).stream())
            .collect(toSet());
      }

      /**
       * For a paren query, we simply require the required terms of our child
       * 
       * Exmaple: (hello) -> [ hello ]
       */
      @Override
      public Set<String> paren(ParenQuery paren) {
        return requiredTokens(paren.getChild());
      }

      /**
       * For a string query, if the term in the string is not a wildcard, then we require the term.
       * 
       * Exmaple: "hello world" -> [ hello, world ]
       * 
       * Example: "hell* world" -> [ world ]
       */
      @Override
      public Set<String> text(TextQuery text) {
        return text.getTerms().stream().filter(t -> !t.isWildcard()).map(t -> t.getText())
            .collect(toSet());
      }

      /**
       * For a vacuous query, there are no required terms, by definition.
       */
      @Override
      public Set<String> vacuous(VacuousQuery vacuous) {
        return emptySet();
      }
    }).process(q);
  }



}
