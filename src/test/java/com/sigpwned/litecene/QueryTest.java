package com.sigpwned.litecene;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.parse.QueryParser;

public class QueryTest {
  @Test
  public void toStringTest() {
    final String input = "hello OR world AND (foobar AND NOT quux)";
    final String output = new QueryParser().query(input).toString();
    assertThat(input, is(output));
  }
}
