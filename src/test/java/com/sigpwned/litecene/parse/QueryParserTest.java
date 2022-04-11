package com.sigpwned.litecene.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.sigpwned.litecene.AndQuery;
import com.sigpwned.litecene.OrQuery;
import com.sigpwned.litecene.Query;
import com.sigpwned.litecene.TermQuery;

public class QueryParserTest {
  /**
   * We should handle a single term
   */
  @Test
  public void test1() {
    Query q = new QueryParser().query("hello");
    assertThat(q, is(new TermQuery("hello")));
  }

  /**
   * We should handle boolean queries
   */
  @Test
  public void test2() {
    Query q = new QueryParser().query("hello OR world AND foobar");
    assertThat(q, is(new OrQuery(asList(new TermQuery("hello"),
        new AndQuery(asList(new TermQuery("world"), new TermQuery("foobar")))))));
  }
}
