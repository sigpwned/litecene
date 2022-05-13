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
package com.sigpwned.litecene.test.example;

import com.sigpwned.litecene.core.CodePointStream;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.QueryPipeline;
import com.sigpwned.litecene.core.TokenStream;
import com.sigpwned.litecene.core.pipeline.query.QueryParser;
import com.sigpwned.litecene.core.pipeline.query.filter.SimplifyQueryFilterPipeline;
import com.sigpwned.litecene.core.stream.codepoint.StringCodePointSource;
import com.sigpwned.litecene.core.stream.codepoint.filter.SmartQuotesCodePointFilter;
import com.sigpwned.litecene.core.stream.token.Tokenizer;
import com.sigpwned.litecene.core.stream.token.filter.text.LetterNumberTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.LowercaseTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.NormalizeTokenFilter;
import com.sigpwned.litecene.core.stream.token.filter.text.PrintableAsciiTokenFilter;
import com.sigpwned.litecene.test.CorpusMatcherTest;

public class ExampleCorpusMatcherTest extends CorpusMatcherTest {
  public ExampleCorpusMatcherTest() {
    super(new ExampleCorpusMatcher());
  }

  @Override
  protected Query parseQuery(String q) {
    // Start with a simple string source
    CodePointStream qp1 = new StringCodePointSource(q);

    // Now rewrite smartquotes
    CodePointStream qp2 = new SmartQuotesCodePointFilter(qp1);

    // Tokenize our code points
    TokenStream qp3 = new Tokenizer(qp2);

    // Normalize our tokens
    TokenStream qp4 = new NormalizeTokenFilter(qp3);

    // Only search printable ASCII
    TokenStream qp5 = new PrintableAsciiTokenFilter(qp4);

    // Only search numbers and letters
    TokenStream qp6 = new LetterNumberTokenFilter(qp5);

    // Match case-insensitively
    TokenStream qp7 = new LowercaseTokenFilter(qp6);

    // Parse a query from our tokens
    QueryPipeline qp8 = new QueryParser(qp7);

    // Simplify our query
    QueryPipeline qp9 = new SimplifyQueryFilterPipeline(qp8);

    return qp9.query();
  }
}
