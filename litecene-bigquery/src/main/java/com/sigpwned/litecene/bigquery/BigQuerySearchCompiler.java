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
package com.sigpwned.litecene.bigquery;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import com.sigpwned.litecene.bigquery.util.BigQueryQueries;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.util.QueryProcessor;
import com.sigpwned.litecene.core.util.Syntax;

public class BigQuerySearchCompiler {
  /**
   * By default, fields are treated as if they are not indexed
   */
  public static final boolean DEFAULT_INDEXED = false;

  /**
   * The fully-qualified field to query, e.g. t.name. The field must be a STRING field analyzed per
   * the readme or else the matching won't work.
   */
  private final String field;

  /**
   * Whether or not the given field has a search index
   */
  private final boolean indexed;

  public BigQuerySearchCompiler(String field) {
    this(field, DEFAULT_INDEXED);
  }

  public BigQuerySearchCompiler(String field, boolean indexed) {
    this.field = field;
    this.indexed = indexed;
  }

  private static final String TOKEN_FIELD_NAME = "t";

  private static final String INDEX_FIELD_NAME = "i";

  @SuppressWarnings("unused")
  private class ProximityTerm {
    public final int index;
    public final String tableName;
    public final String tableExpr;

    public ProximityTerm(int index, String tableName, String tableExpr) {
      this.index = index;
      this.tableName = tableName;
      this.tableExpr = tableExpr;
    }
  }

  /**
   * Returns a predicate that can be used to match the given query against the text in the given
   * field.
   */
  public String compile(Query q) {
    if (BigQueryQueries.isFullySearchable(q)) {
      return String.format("(%s)", searchPredicate(q));
    } else if (isIndexed()) {
      return String.format("((%s) AND (%s))", searchPredicate(q), regexPredicate(q));
    } else {
      return String.format("(%s)", regexPredicate(q));
    }
  }

  /**
   * BigQuery is inching towards full-text search indexing. For now, we can only filter by terms
   * that must all appear in the text being searched. This implements that predicate when possible.
   */
  private String searchPredicate(Query q) {
    Set<String> requiredTokens = BigQueryQueries.requiredTokens(q);
    if (requiredTokens.isEmpty()) {
      // There are no tokens required by the query. Trivially match.
      return "TRUE";
    } else {
      // There are tokens required by the query. Search for them.
      String result = String.format("SEARCH(%s, '%s')", getField(),
          requiredTokens.stream().sorted().collect(joining(" ")));
      System.out.println(result);
      return result;
    }
  }

  /**
   * Returns a BigQuery predicate that searches the given query for this instance's field for the
   * given Query using regular expressions
   */
  private String regexPredicate(Query q) {
    String result = new QueryProcessor<String>(new QueryProcessor.Processor<String>() {
      @Override
      public String and(AndQuery and) {
        return and.getChildren().stream().map(c -> "(" + regexPredicate(c) + ")")
            .collect(joining(" AND "));
      }

      @Override
      public String or(OrQuery or) {
        return or.getChildren().stream().map(c -> "(" + regexPredicate(c) + ")")
            .collect(joining(" OR "));
      }

      @Override
      public String not(NotQuery not) {
        return String.format("NOT (%s)", regexPredicate(not.getChild()));
      }

      @Override
      public String list(ListQuery list) {
        return list.getChildren().stream().map(c -> "(" + regexPredicate(c) + ")")
            .collect(joining(" AND "));
      }

      @Override
      public String paren(ParenQuery paren) {
        return String.format("(%s)", regexPredicate(paren.getChild()));
      }

      /**
       * We use a combination of regular expressions to find the string tokens
       */
      @Override
      public String text(TextQuery text) {
        if (text.getProximity().isPresent()) {
          // The tokens don't have to be in order, but they do have to be close to each other. We
          // create a table of acceptable tokens for each search term and then filter the cartesian
          // product based on proximity.
          int proximity = text.getProximity().getAsInt();

          List<ProximityTerm> terms = IntStream.range(0, text.getTerms().size())
              .mapToObj(i -> proximityTermFromTermIndex(i, text.getTerms().get(i)))
              .collect(toList());

          return String.format("EXISTS (SELECT 1 FROM %s WHERE GREATEST(%s)-LEAST(%s)+1 <= %d)",
              terms.stream().map(t -> t.tableExpr).collect(joining(" CROSS JOIN ")),
              terms.stream().map(t -> t.tableName + "." + INDEX_FIELD_NAME).collect(joining(", ")),
              terms.stream().map(t -> t.tableName + "." + INDEX_FIELD_NAME).collect(joining(", ")),
              proximity);
        } else {
          // The tokens must be in order. We simply search for all the regular expressions in order
          return String.format("REGEXP_CONTAINS(%s, r\"%s\")", field,
              text.getTerms().stream().map(t -> pattern(t)).collect(joining(" ")));
        }
      }

      /**
       * Always match
       */
      @Override
      public String vacuous(VacuousQuery vacuous) {
        return "TRUE";
      }
    }).process(q);
    System.out.println(result);
    return result;
  }

  private String pattern(Term term) {
    return pattern(term.getText(), term.isWildcard());
  }

  private String pattern(String text, boolean wildcard) {
    StringBuilder pattern = new StringBuilder().append("\\b\\Q").append(text).append("\\E");
    if (wildcard)
      pattern.append("[a-z0-9]*");
    pattern.append("\\b");
    return pattern.toString();
  }

  private ProximityTerm proximityTermFromTermIndex(int i, Term ti) {
    // These are just useful aliases
    String tableName = "_q" + i;

    int tokenCount = Math.toIntExact(Syntax.WHITESPACE.splitAsStream(ti.getText()).count());

    // This produces a table of all the tokens in the given field text with its index
    String tableExpr;
    if (tokenCount == 1) {
      // If there's only one token, then we can just compare the term text to the individual words
      // extracted by the regex to compare to the term
      tableExpr = String.format(
          "(SELECT %s, %s FROM UNNEST(REGEXP_EXTRACT_ALL(%s, r\"[a-z0-9]+\")) AS %s WITH OFFSET %s WHERE REGEXP_CONTAINS(%s, r\"%s\")) AS %s",
          INDEX_FIELD_NAME, TOKEN_FIELD_NAME, getField(), TOKEN_FIELD_NAME, INDEX_FIELD_NAME,
          TOKEN_FIELD_NAME, pattern(ti), tableName);
    } else {
      // If there's more than one token, then we have to use the ML.NGRAMS function to pull in N
      // adjacent tokens to compare to the term
      tableExpr = String.format(
          "(SELECT %s, %s FROM UNNEST(ML.NGRAMS(REGEXP_EXTRACT_ALL(%s, r\"[a-z0-9]+\"), [ %d, %d ])) AS %s WITH OFFSET %s WHERE REGEXP_CONTAINS(%s, r\"%s\")) AS %s",
          INDEX_FIELD_NAME, TOKEN_FIELD_NAME, getField(), tokenCount, tokenCount, TOKEN_FIELD_NAME,
          INDEX_FIELD_NAME, TOKEN_FIELD_NAME, pattern(ti), tableName);
    }

    return new ProximityTerm(i, tableName, tableExpr);
  }

  /**
   * @return the field
   */
  public String getField() {
    return field;
  }

  /**
   * @return the indexed
   */
  public boolean isIndexed() {
    return indexed;
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, indexed);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BigQuerySearchCompiler other = (BigQuerySearchCompiler) obj;
    return Objects.equals(field, other.field) && indexed == other.indexed;
  }

  @Override
  public String toString() {
    return "BigQuerySearchCompiler [field=" + field + ", indexed=" + indexed + "]";
  }
}
