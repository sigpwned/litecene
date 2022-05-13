package com.sigpwned.litecene.core.pipeline.query.filter;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.OptionalInt;
import org.junit.Test;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.pipeline.query.QueryParser;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.stream.token.Tokenizer;

public class SimplifyQueryFilterPipelineTest {
  public static Query parseQuery(String s) {
    return new SimplifyQueryFilterPipeline(
        new QueryParser(new Tokenizer(new StringCodePointSource(s)))).query();
  }

  @Test
  public void shouldRemoveVacuousAndTerm() {
    Query simplifiedQuery = parseQuery("\"\" AND world");
    assertThat(simplifiedQuery,
        is(new TextQuery(asList(Term.fromString("world")), OptionalInt.empty())));
  }

  @Test
  public void shouldRemoveVacuousOrTerm() {
    Query simplifiedQuery = parseQuery("\"\" OR world");
    assertThat(simplifiedQuery,
        is(new TextQuery(asList(Term.fromString("world")), OptionalInt.empty())));
  }

  @Test
  public void shouldRemoveVacuousNotTerm() {
    Query simplifiedQuery = parseQuery("NOT \"\"");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }

  @Test
  public void shouldRemoveVacuousGroupTerm() {
    Query simplifiedQuery = parseQuery("(\"\")");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }

  @Test
  public void shouldRemoveVacuousTerm() {
    Query simplifiedQuery = parseQuery("\"\"");
    assertThat(simplifiedQuery, is(VacuousQuery.INSTANCE));
  }
}
