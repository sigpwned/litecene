package com.sigpwned.litecene.core.util;

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.litecene.core.Token;
import com.sigpwned.litecene.core.TokenStream;

public final class TokenStreams {
  private TokenStreams() {}

  /**
   * Drains a {@link TokenStream} to a {@link List}.
   */
  public static List<Token> toList(TokenStream ts) {
    List<Token> result = new ArrayList<>();
    while (ts.hasNext()) {
      result.add(ts.next());
    }
    return result;
  }
}
