package com.sigpwned.litecene.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.sigpwned.litecene.exception.EofException;

public class QueryTokenizerTest {
  @Test
  public void tokensTest() {
    QueryTokenizer ts = new QueryTokenizer("  hello world AND NOT OR ( ) 1234 \"yo dawg\"  ");

    List<Token> tokens = new ArrayList<>();
    do {
      tokens.add(ts.next());
    } while (tokens.get(tokens.size() - 1).getType() != Token.Type.EOF);

    assertThat(tokens,
        is(asList(new Token(Token.Type.TERM, "hello"), new Token(Token.Type.TERM, "world"),
            Token.AND, Token.NOT, Token.OR, Token.LPAREN, Token.RPAREN,
            new Token(Token.Type.TERM, "1234"), new Token(Token.Type.STRING, "yo dawg"),
            Token.EOF)));

  }

  @Test(expected = EofException.class)
  public void unclosedString() {
    QueryTokenizer ts = new QueryTokenizer("\"this string is not closed.");
    for (Token t = ts.next(); t.getType() != Token.Type.EOF; t = ts.next()) {
      // Ignore...
    }
  }
}
