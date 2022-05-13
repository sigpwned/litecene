package com.sigpwned.litecene.core.stream.token.filter;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.OptionalInt;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.query.token.TextToken;
import com.sigpwned.litecene.core.stream.token.TokenFilter;
import com.sigpwned.litecene.core.util.Terms;

/**
 * Rewrite text tokens only (i.e., term and phrase)
 */
public abstract class TextProcessingTokenFilter extends TokenFilter {
  public TextProcessingTokenFilter(TokenStream upstream) {
    super(upstream);
  }

  @Override
  protected final Token filter(Token token) {
    switch (token.getType()) {
      case TEXT: {
        TextToken text = token.asText();

        List<Term> originalTerms = text.getTerms();

        int originalSize = originalTerms.stream().mapToInt(Terms::size).sum();

        List<Term> processedTerms =
            originalTerms.stream().map(t -> new Term(process(t.getText()), t.isWildcard()))
                .filter(t -> !Terms.isVacuous(t)).collect(toList());

        int processedSize = processedTerms.stream().mapToInt(Terms::size).sum();

        if (text.getProximity().isPresent()) {
          int originalProximity = text.getProximity().getAsInt();
          int processedProximity = originalProximity + processedSize - originalSize;
          return new TextToken(processedTerms, processedProximity);
        } else {
          return new TextToken(processedTerms, OptionalInt.empty());
        }
      }
      case AND:
      case EOF:
      case LPAREN:
      case NOT:
      case OR:
      case RPAREN:
      default:
        // These are not text tokens, so we don't process them
        return token;
    }
  }

  protected abstract String process(String text);
}
