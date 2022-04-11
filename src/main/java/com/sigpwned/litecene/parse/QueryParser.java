package com.sigpwned.litecene.parse;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.litecene.AndQuery;
import com.sigpwned.litecene.NotQuery;
import com.sigpwned.litecene.OrQuery;
import com.sigpwned.litecene.ParenQuery;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.StringQuery;
import com.sigpwned.litecene.TermQuery;
import com.sigpwned.litecene.exception.EofException;
import com.sigpwned.litecene.exception.UnmatchedParenthesisException;
import com.sigpwned.litecene.exception.UnrecognizedCharacterException;

public class QueryParser {
  public Query query(String s) {
    return query1(QueryTokenizer.forString(s));
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

  // NOT X
  private Query query3(QueryTokenizer ts) {
    if (ts.peek().getType() == Token.Type.NOT) {
      ts.next(); // NOT
      return new NotQuery(query4(ts));
    } else {
      return query4(ts);
    }
  }

  // atoms
  private Query query4(QueryTokenizer ts) {
    return atom(ts);
  }

  private Query atom(QueryTokenizer ts) {
    Token t = ts.next();
    switch (t.getType()) {
      case TERM:
        return new TermQuery(t.getText());
      case STRING:
        return new StringQuery(t.getText(), null);
      case LPAREN: {
        Query result = query1(ts);
        if (ts.next().getType() != Token.Type.RPAREN)
          throw new UnmatchedParenthesisException();
        return new ParenQuery(result);
      }
      case EOF:
        throw new EofException();
      default:
        throw new UnrecognizedCharacterException();
    }
  }
}
