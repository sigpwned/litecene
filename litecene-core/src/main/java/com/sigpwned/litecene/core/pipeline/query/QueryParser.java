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
package com.sigpwned.litecene.core.pipeline.query;

import static java.util.Collections.unmodifiableSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.exception.EofException;
import com.sigpwned.litecene.core.exception.UnmatchedParenthesisException;
import com.sigpwned.litecene.core.exception.UnparsedTokenException;
import com.sigpwned.litecene.core.exception.UnrecognizedTokenException;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.query.token.TextToken;

public class QueryParser implements QueryPipeline {
  private final TokenStream ts;

  public QueryParser(TokenStream ts) {
    this.ts = ts;
  }

  public Query query() {
    Query result;
    if (ts.peek().getType() == Token.Type.EOF) {
      result = VacuousQuery.INSTANCE;
    } else {
      result = query1();
    }
    if (ts.peek().getType() != Token.Type.EOF)
      throw new UnparsedTokenException();
    return result;
  }

  // X OR Y OR Z ...
  private Query query1() {
    Query q = query2();
    if (ts.peek().getType() == Token.Type.OR) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        ts.next(); // OR
        children.add(query2());
      } while (ts.peek().getType() == Token.Type.OR);
      return new OrQuery(children);
    } else {
      return q;
    }
  }

  // X AND Y AND Z ...
  private Query query2() {
    Query q = query3();
    if (ts.peek().getType() == Token.Type.AND) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        ts.next(); // AND
        children.add(query3());
      } while (ts.peek().getType() == Token.Type.AND);
      return new AndQuery(children);
    } else {
      return q;
    }
  }

  private static final Set<Token.Type> LISTABLES =
      unmodifiableSet(EnumSet.of(Token.Type.LPAREN, Token.Type.NOT, Token.Type.TEXT));

  // term term term ...
  private Query query3() {
    Query q = query4();
    if (LISTABLES.contains(ts.peek().getType())) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        children.add(query4());
      } while (LISTABLES.contains(ts.peek().getType()));
      return new ListQuery(children);
    } else {
      return q;
    }
  }

  // NOT X
  private Query query4() {
    if (ts.peek().getType() == Token.Type.NOT) {
      ts.next(); // NOT
      return new NotQuery(query4());
    } else {
      return query5();
    }
  }

  // atoms
  private Query query5() {
    return atom();
  }

  private Query atom() {
    Token t = ts.next();
    switch (t.getType()) {
      case TEXT: {
        TextToken tt = t.asText();
        if (tt.getTerms().isEmpty()) {
          return VacuousQuery.INSTANCE;
        } else {
          return new TextQuery(tt.getTerms(), tt.getProximity());
        }
      }
      case LPAREN: {
        Query result = query1();
        if (ts.next().getType() != Token.Type.RPAREN)
          throw new UnmatchedParenthesisException();
        return new ParenQuery(result);
      }
      case EOF:
        throw new EofException();
      // $CASES-OMITTED$
      default:
        throw new UnrecognizedTokenException();
    }
  }
}
