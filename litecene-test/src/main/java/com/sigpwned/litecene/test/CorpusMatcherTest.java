/*-
 * =================================LICENSE_START==================================
 * litecene-test
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.litecene.test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sigpwned.litecene.core.Query;

public abstract class CorpusMatcherTest {
  public static Corpus corpus;

  public static final String PIRATE_ID = "pirate";

  public static final String CUPCAKE_ID = "cupcake";

  public static final String CHEESE_ID = "cheese";

  public static final String HIPSTER_ID = "hipster";

  public static final String OFFICE_ID = "office";

  @BeforeClass
  public static void setupQueryMatchTest() {
    // https://pirateipsum.me/
    Document pirate = Document.of(PIRATE_ID,
        "Prow scuttle parrel provost Sail ho shrouds spirits boom mizzenmast yardarm. Pinnace holystone mizzenmast quarter crow's nest nipperkin grog yardarm hempen halter ipsum furl. Swab barque interloper chantey doubloon starboard grog black jack gangway rutters.\n");

    // http://www.cupcakeipsum.com/
    Document cupcake = Document.of(CUPCAKE_ID,
        "Cupcake ipsum dolor sit amet pudding. Cake ice cream apple pie jelly donut lemon drops muffin ice cream. Brownie shortbread gingerbread sweet ipsum croissant candy chocolate bar jelly. Sweet cake cotton candy caramels oat cake cheesecake jelly-o.\n");

    // http://www.cheeseipsum.co.uk/
    Document cheese = Document.of(CHEESE_ID,
        "Everyone loves cheesy feet brie. Cauliflower cheese melted cheese fromage frais danish fontina parmesan feta mozzarella melted cheese. Jarlsberg boursin ipsum melted cheese goat chalk and cheese cheeseburger caerphilly the big cheese. Cheddar cheese on toast.\n");

    // https://hipsum.co/
    Document hipster = Document.of(HIPSTER_ID,
        "Venmo pok pok man braid hella XOXO copper mug. Jean shorts XOXO freegan, jianbing forage bitters shoreditch mixtape celiac ugh ipsum bespoke health goth. Street art four dollar toast portland salvia vice, pabst squid mustache farm-to-table edison bulb tousled bespoke kogi seitan. Scenester cornhole put a bird on it, lyft hammock hella trust fund wayfarers. Farm-to-table waistcoat chia 3 wolf moon sustainable craft beer semiotics whatever offal post-ironic. Occupy deep v brooklyn cred neutra thundercats. Yr vexillologist woke tacos skateboard keytar ethical farm-to-table.");

    // http://officeipsum.com/
    Document office = Document.of(OFFICE_ID,
        "Ramp up let's put a pin in that so up the flagpole bazooka that run it past the boss jump right in and banzai attack will they won't they ipsum its all greek to me unless they bother until the end of time maybe vis a vis too many cooks over the line. Rock Star/Ninja when does this sunset? or we should have a meeting to discuss the details of the next meeting quick sync so looks great, can we try it a different way. Put it on the parking lot deep dive. We can't hear you do i have consent to record this meeting.");

    corpus = Corpus.of(asList(pirate, cupcake, cheese, hipster, office));
  }

  public CorpusMatcher matcher;

  public CorpusMatcherTest(CorpusMatcher matcher) {
    this.matcher = matcher;
  }

  @Test
  public void shouldAllMatchIpsum() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("ipsum"));
    assertThat(matchIds, is(Set.of(PIRATE_ID, CUPCAKE_ID, CHEESE_ID, HIPSTER_ID, OFFICE_ID)));
  }

  @Test
  public void shouldMatchCheeseOnly1() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("fontina"));
    assertThat(matchIds, is(Set.of(CHEESE_ID)));
  }

  @Test
  public void shouldMatchCheeseOnly2() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("fontina AND (\"melted cheese\")"));
    assertThat(matchIds, is(Set.of(CHEESE_ID)));
  }

  @Test
  public void shouldMatchCheeseOnly3() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("font*"));
    assertThat(matchIds, is(Set.of(CHEESE_ID)));
  }

  @Test
  public void shouldMatchCheeseOnly4() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("\"font*\" AND \"melted cheese\""));
    assertThat(matchIds, is(Set.of(CHEESE_ID)));
  }

  @Test
  public void shouldMatchPiratesOnly1() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("\"spirits mizzenmast\"~4"));
    assertThat(matchIds, is(Set.of(PIRATE_ID)));
  }

  @Test
  public void shouldMatchPiratesOnly2() throws IOException {
    // The "crow's" token will get split into two parts, "crow s", in any query handler that ignores
    // punctuation. We need to make sure that case get's handled appropriately.
    Set<String> matchIds = matcher.match(corpus, parseQuery("\"crow's nest\"~4"));
    assertThat(matchIds, is(Set.of(PIRATE_ID)));
  }

  @Test
  public void shouldNotMatchPiratesOnly1() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("\"spirits mizzenmast\"~2"));
    assertThat(matchIds, is(Set.of()));
  }

  @Test
  public void shouldMatchCheeseAndPirate() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("fontina OR mizzenmast"));
    assertThat(matchIds, is(Set.of(CHEESE_ID, PIRATE_ID)));
  }

  @Test
  public void shouldMatchNothing1() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("fontina AND mizzenmast"));
    assertThat(matchIds, is(Set.of()));
  }

  @Test
  public void shouldMatchNothing2() throws IOException {
    Set<String> matchIds = matcher.match(corpus, parseQuery("fontina mizzenmast"));
    assertThat(matchIds, is(Set.of()));
  }

  protected abstract Query parseQuery(String q);
}
