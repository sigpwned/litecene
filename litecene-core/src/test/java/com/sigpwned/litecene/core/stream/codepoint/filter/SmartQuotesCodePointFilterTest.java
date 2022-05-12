package com.sigpwned.litecene.core.stream.codepoint.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;

public class SmartQuotesCodePointFilterTest {
  @Test
  public void shouldReplaceSmartquotes() {
    CodePointStream cps =
        new SmartQuotesCodePointFilter(new StringCodePointSource("\u201Chello world\u201D"));

    StringBuilder buf = new StringBuilder();
    while (cps.hasNext())
      buf.appendCodePoint(cps.next());

    assertThat(buf.toString(), is("\"hello world\""));
  }
}
