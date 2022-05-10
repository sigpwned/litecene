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
package com.sigpwned.litecene.query.parse;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.exception.EofException;
import com.sigpwned.litecene.exception.UnmatchedParenthesisException;
import com.sigpwned.litecene.exception.UnparsedTokenException;
import com.sigpwned.litecene.exception.UnrecognizedTokenException;
import com.sigpwned.litecene.query.AndQuery;
import com.sigpwned.litecene.query.ListQuery;
import com.sigpwned.litecene.query.NotQuery;
import com.sigpwned.litecene.query.OrQuery;
import com.sigpwned.litecene.query.ParenQuery;
import com.sigpwned.litecene.query.StringQuery;
import com.sigpwned.litecene.query.TermQuery;
import com.sigpwned.litecene.query.parse.token.StringToken;
import com.sigpwned.litecene.query.parse.token.TermToken;

public class QueryParser {
  public Query query(String s) {
    QueryTokenizer ts = QueryTokenizer.forString(s);
    Query result = query1(ts);
    if (ts.peek().getType() != Token.Type.EOF)
      throw new UnparsedTokenException();
    return result;
  }

  // X OR Y OR Z ...
  private Query query1(QueryTokenizer ts) {
    Query q = query2(ts);
    if (ts.peek().getType() == Token.Type.OR) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        ts.next(); // OR
        children.add(query2(ts));
      } while (ts.peek().getType() == Token.Type.OR);
      return new OrQuery(children);
    } else {
      return q;
    }
  }

  // X AND Y AND Z ...
  private Query query2(QueryTokenizer ts) {
    Query q = query3(ts);
    if (ts.peek().getType() == Token.Type.AND) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        ts.next(); // AND
        children.add(query3(ts));
      } while (ts.peek().getType() == Token.Type.AND);
      return new AndQuery(children);
    } else {
      return q;
    }
  }

  private static final Set<Token.Type> LISTABLES = unmodifiableSet(
      EnumSet.of(Token.Type.LPAREN, Token.Type.NOT, Token.Type.STRING, Token.Type.TERM));

  // term term term ...
  private Query query3(QueryTokenizer ts) {
    Query q = query4(ts);
    if (LISTABLES.contains(ts.peek().getType())) {
      List<Query> children = new ArrayList<>();
      children.add(q);
      do {
        children.add(query4(ts));
      } while (LISTABLES.contains(ts.peek().getType()));
      return new ListQuery(children);
    } else {
      return q;
    }
  }

  // NOT X
  private Query query4(QueryTokenizer ts) {
    if (ts.peek().getType() == Token.Type.NOT) {
      ts.next(); // NOT
      return new NotQuery(query5(ts));
    } else {
      return query5(ts);
    }
  }

  // atoms
  private Query query5(QueryTokenizer ts) {
    return atom(ts);
  }

  private Query atom(QueryTokenizer ts) {
    Token t = ts.next();
    switch (t.getType()) {
      case TERM: {
        TermToken tt = t.asTerm();
        return new TermQuery(tt.getText(), tt.isWildcard());
      }
      case STRING: {
        StringToken st = t.asString();
        return new StringQuery(
            st.getTerms().stream().map(stt -> new StringQuery.Term(stt.getText(), stt.isWildcard()))
                .collect(toList()),
            st.getProxmity().isPresent() ? st.getProxmity().getAsInt() : null);
      }
      case LPAREN: {
        Query result = query1(ts);
        if (ts.next().getType() != Token.Type.RPAREN)
          throw new UnmatchedParenthesisException();
        return new ParenQuery(result);
      }
      case EOF:
        throw new EofException();
      default:
        throw new UnrecognizedTokenException();
    }
  }
}
