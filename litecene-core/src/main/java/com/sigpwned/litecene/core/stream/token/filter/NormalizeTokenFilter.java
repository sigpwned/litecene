package com.sigpwned.litecene.core.stream.token.filter;

import java.text.Normalizer;
import java.util.regex.Pattern;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.PhraseToken;
import com.sigpwned.litecene.core.query.token.TermToken;
import com.sigpwned.litecene.core.stream.token.TokenFilter;

/**
 * Replaces text tokens with "simplified" latin representations. For example, replace "fůňķŷ" with
 * "funky".
 */
public class NormalizeTokenFilter extends TokenFilter {
  public NormalizeTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  /**
   * Recognizes a single character in the Unicode mark category
   */
  private static final Pattern MARK = Pattern.compile("\\p{M}");

  /**
   * Recognizes a run of one or more non-whitespace characters, which is to say a token
   */
  private static final Pattern TOKEN = Pattern.compile("[^\\p{javaWhitespace}]+");

  @Override
  protected Token filter(Token token) {
    switch (token.getType()) {
      case PHRASE: {
        PhraseToken phrase = token.asPhrase();

        String originalText = phrase.getText();

        int originalTokenCount =
            Math.toIntExact(TOKEN.matcher(originalText.strip()).results().count());

        String normalizedText = Normalizer.normalize(phrase.getText(), Normalizer.Form.NFKD);
        String rewrittenText = MARK.matcher(normalizedText).replaceAll("");

        int rewrittenTokenCount =
            Math.toIntExact(TOKEN.matcher(originalText.strip()).results().count());

        Integer proximity;
        if (phrase.getProximity().isPresent()) {
          if (rewrittenTokenCount < originalTokenCount) {
            // TODO WARNING A token disappeared
            proximity = phrase.getProximity().getAsInt() + rewrittenTokenCount - originalTokenCount;
          } else {
            proximity = phrase.getProximity().getAsInt();
          }
        } else {
          proximity = null;
        }

        return new PhraseToken(rewrittenText, proximity);
      }
      case TERM: {
        TermToken term = token.asTerm();
        String originalText = term.getText();
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFKD);
        String rewrittenText = MARK.matcher(normalizedText).replaceAll("");
        if (rewrittenText.isBlank()) {
          // TODO WARNING Term is now empty
        }
        return new TermToken(rewrittenText);
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
