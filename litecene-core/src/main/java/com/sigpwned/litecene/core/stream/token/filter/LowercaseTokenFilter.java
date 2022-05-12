package com.sigpwned.litecene.core.stream.token.filter;

import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.PhraseToken;
import com.sigpwned.litecene.core.query.token.TermToken;
import com.sigpwned.litecene.core.stream.token.TokenFilter;

/**
 * Converts text tokens to their lowercase representations
 */
public class LowercaseTokenFilter extends TokenFilter {
  public LowercaseTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  @Override
  protected Token filter(Token token) {
    switch (token.getType()) {
      case PHRASE: {
        PhraseToken phrase = token.asPhrase();
        return new PhraseToken(phrase.getText().toLowerCase(), phrase.getProximity());
      }
      case TERM: {
        TermToken term = token.asTerm();
        return new TermToken(term.getText().toLowerCase());
      }
      case AND:
      case EOF:
      case LPAREN:
      case NOT:
      case OR:
      case RPAREN:
      default:
        // Pass all these right through
        return token;
    }
  }
}
