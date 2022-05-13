package com.sigpwned.litecene.core.stream.token;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.ListIterator;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;

/**
 * Returns a fixed list of {@link Token} objects. Useful for testing.
 */
public class ListTokenSource implements TokenStream {
  private final ListIterator<Token> iterator;

  public ListTokenSource(List<Token> tokens) {
    this.iterator = unmodifiableList(tokens).listIterator();
  }

  @Override
  public Token peek() {
    if (getIterator().hasNext()) {
      Token result = getIterator().next();
      getIterator().previous();
      return result;
    } else {
      return Token.EOF;
    }
  }

  @Override
  public Token next() {
    if (getIterator().hasNext()) {
      return getIterator().next();
    } else {
      return Token.EOF;
    }
  }

  /**
   * @return the iterator
   */
  private ListIterator<Token> getIterator() {
    return iterator;
  }
}
