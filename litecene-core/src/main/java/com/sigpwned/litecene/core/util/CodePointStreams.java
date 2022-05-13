package com.sigpwned.litecene.core.util;

import com.sigpwned.litecene.core.CodePointStream;

public final class CodePointStreams {
  private CodePointStreams() {}

  /**
   * Drains a {@link CodePointStream} to a {@link String}.
   */
  public static String toString(CodePointStream cps) {
    StringBuilder result = new StringBuilder();
    while (cps.hasNext())
      result.appendCodePoint(cps.next());
    return result.toString();
  }
}
