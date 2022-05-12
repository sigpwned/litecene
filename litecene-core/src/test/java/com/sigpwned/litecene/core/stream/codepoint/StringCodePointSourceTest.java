package com.sigpwned.litecene.core.stream.codepoint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.core.CodePointStream;

public class StringCodePointSourceTest {
  @Test
  public void shouldReturnAllCodePointsInOrder() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    StringBuilder buf = new StringBuilder();
    while (cps.hasNext())
      buf.appendCodePoint(cps.next());

    assertThat(buf.toString(), is(input));
    assertThat(cps.peek(), is(CodePointStream.EOF));
  }

  @Test
  public void twoPeeksShouldReturnSameValue() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    int peek1 = cps.peek();
    int peek2 = cps.peek();

    assertThat(peek2, is(peek1));
  }

  @Test
  public void twoNextsShouldReturnDifferentValue() {
    String input = "Hello, world!";

    CodePointStream cps = new StringCodePointSource(input);

    int next1 = cps.next();
    int next2 = cps.next();

    assertThat(next2, not(is(next1)));
  }
}
