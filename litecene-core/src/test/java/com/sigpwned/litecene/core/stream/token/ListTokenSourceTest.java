package com.sigpwned.litecene.core.stream.token;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.util.TokenStreams;

public class ListTokenSourceTest {
  @Test
  public void shouldReturnTokenList() {
    List<Token> inputTokens = asList(Token.AND, Token.LPAREN, Token.OR, Token.NOT, Token.RPAREN,
        new TextToken(asList(Term.fromString("füñkÿ123#z")), OptionalInt.empty()));

    TokenStream source = new ListTokenSource(inputTokens);

    List<Token> outputTokens = TokenStreams.toList(source);

    assertThat(outputTokens, is(inputTokens));
  }
}
