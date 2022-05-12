package com.sigpwned.litecene.core.stream.token.filter;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.PhraseToken;
import com.sigpwned.litecene.core.query.token.TermToken;
import com.sigpwned.litecene.core.stream.token.TokenFilter;

/**
 * Re-tokenizes text tokens to use letters and numbers only. This can be combined with the
 * {@link NormalizeTokenFilter} to create an "ASCII alnum" filter.
 */
public class LetterNumberTokenFilter extends TokenFilter {
  public LetterNumberTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  private static final Pattern ALNUM = Pattern.compile("[\\p{L}\\p{N}]+");

  private static final Pattern SPACES = Pattern.compile("\\p{javaWhitespace}+");

  @Override
  protected Token filter(Token token) {
    switch (token.getType()) {
      case TERM: {
        TermToken term = token.asTerm();
        return retokenize(term.getText(), null);
      }
      case PHRASE: {
        PhraseToken phrase = token.asPhrase();
        return retokenize(phrase.getText(),
            phrase.getProximity().isPresent() ? phrase.getProximity().getAsInt() : null);
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

  private Token retokenize(String text, Integer proximity) {
    int originalTokenCount = Math.toIntExact(SPACES.splitAsStream(text.strip()).count());

    List<String> tokens = tokenize(text);

    int rewrittenTokenCount = tokens.size();

    if (tokens.isEmpty()) {
      // I don't care if we have proximity or not. We are now vacuous.
      if (originalTokenCount > 0) {
        // TODO WARNING token disappeared
      }
      return new TermToken("");
    } else if (tokens.size() == 1 && proximity == null) {
      // This can be adequately represented as a term token
      return new TermToken(tokens.get(0));
    } else {
      // Because we analyze the query text, the number of terms may not match the number of
      // tokens. The number of terms can be more (what's -> what, s) or less (&#$ is not a
      // valid term) than number of tokens. Therefore, if the user gave a proximity, we'll
      // want to adjust that count appropriately. The adjusted proximity can never reach an invalid
      // value -- 0 or negative -- because (a) we check for empty tokens above, and (b) we adjust
      // based on how many tokens (dis)appeared.
      if (proximity != null)
        proximity = proximity + rewrittenTokenCount - originalTokenCount;
      return new PhraseToken(String.join(" ", tokens), proximity);
    }
  }

  private List<String> tokenize(String text) {
    boolean wildcard;
    if (text.endsWith("*")) {
      text = text.substring(0, text.length() - 1);
      wildcard = true;
    } else {
      wildcard = false;
    }

    if (text.contains("*")) {
      // TODO WARNING ignored wildcard
    }

    List<String> terms = ALNUM.matcher(text).results().map(MatchResult::group)
        .filter(s -> !s.isEmpty()).collect(toList());

    if (wildcard)
      terms.set(terms.size() - 1, terms.get(terms.size() - 1) + "*");

    return terms;
  }
}
