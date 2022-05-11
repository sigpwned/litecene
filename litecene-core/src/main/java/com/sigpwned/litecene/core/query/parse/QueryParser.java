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
package com.sigpwned.litecene.core.query.parse;

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
import com.sigpwned.litecene.core.Term;
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
import com.sigpwned.litecene.core.query.parse.token.PhraseToken;
import com.sigpwned.litecene.core.query.parse.token.TermToken;

public class QueryParser {
  public Query query(QueryTokenizer ts) {
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
      EnumSet.of(Token.Type.LPAREN, Token.Type.NOT, Token.Type.PHRASE, Token.Type.TERM));

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

  private static final Pattern ALNUM = Pattern.compile("[a-zA-Z0-9]+");

  private static final Pattern SPACES = Pattern.compile("\\p{javaWhitespace}+");

  private Query atom(QueryTokenizer ts) {
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
        PhraseToken pt = t.asString();

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
