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
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.Term;
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
import com.sigpwned.litecene.core.query.PhraseQuery;
import com.sigpwned.litecene.core.query.TermQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.query.token.PhraseToken;
import com.sigpwned.litecene.core.query.token.TermToken;

public class QueryParser implements QueryPipeline {
  private final TokenStream ts;

  public QueryParser(TokenStream ts) {
    this.ts = ts;
  }

  public Query query() {
    Query result = query1();
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

  private static final Set<Token.Type> LISTABLES = unmodifiableSet(
      EnumSet.of(Token.Type.LPAREN, Token.Type.NOT, Token.Type.PHRASE, Token.Type.TERM));

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
      return new NotQuery(query5());
    } else {
      return query5();
    }
  }

  // atoms
  private Query query5() {
    return atom();
  }

  private static final Pattern ALNUM = Pattern.compile("[a-zA-Z0-9]+");

  private static final Pattern SPACES = Pattern.compile("\\p{javaWhitespace}+");

  private Query atom() {
    Token t = ts.next();
    switch (t.getType()) {
      case TERM: {
        TermToken tt = t.asTerm();

        String text = tt.getText();

        List<Term> terms = termify(text);

        if (terms.size() == 0) {
          return VacuousQuery.INSTANCE;
        } else if (terms.size() == 1) {
          return new TermQuery(terms.get(0));
        } else {
          return new PhraseQuery(terms, OptionalInt.empty());
        }
      }
      case PHRASE: {
        PhraseToken pt = t.asPhrase();

        String text = pt.getText();

        List<String> tokens = SPACES.splitAsStream(text).collect(toList());

        List<Term> terms = tokens.stream().flatMap(s -> termify(s).stream()).collect(toList());

        if (terms.size() == 0) {
          return VacuousQuery.INSTANCE;
        } else {
          if (pt.getProximity().isPresent()) {
            // Because we analyze the query text, the number of terms may not match the number of
            // tokens. The number of terms can be more (what's -> what, s) or less (&#$ is not a
            // valid term) than number of tokens. Therefore, if the user gave a proximity, we'll
            // want to adjust that count appropriately.
            int proximity = pt.getProximity().getAsInt() + terms.size() - tokens.size();
            if (proximity < terms.size()) {
              // If we have 5 terms that must be within 4 of each other, obviously that's not
              // possible. We consider that to be a degenerate case, and return a vacuous query.
              // TODO Warning degenerate proximity
              return VacuousQuery.INSTANCE;
            } else {
              return new PhraseQuery(terms, proximity);
            }
          } else {
            // If there is no proximity, then we're done here.
            return new PhraseQuery(terms, OptionalInt.empty());
          }
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
      default:
        throw new UnrecognizedTokenException();
    }
  }

  private List<Term> termify(String token) {
    boolean wildcard;
    if (token.endsWith("*")) {
      token = token.substring(0, token.length() - 1);
      wildcard = true;
    } else {
      wildcard = false;
    }

    if (token.contains("*")) {
      // TODO Warning ignored wildcard
    }

    List<Term> terms = ALNUM.matcher(token).results().map(MatchResult::group)
        .filter(s -> !s.isEmpty()).map(s -> Term.of(s, false)).collect(toList());

    if (wildcard)
      terms.set(terms.size() - 1, Term.of(terms.get(terms.size() - 1).getText(), true));

    return terms;
  }
}
