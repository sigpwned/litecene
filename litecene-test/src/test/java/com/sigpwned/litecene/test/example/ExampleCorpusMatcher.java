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

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import com.sigpwned.litecene.core.Query;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.query.AndQuery;
import com.sigpwned.litecene.core.query.ListQuery;
import com.sigpwned.litecene.core.query.NotQuery;
import com.sigpwned.litecene.core.query.OrQuery;
import com.sigpwned.litecene.core.query.ParenQuery;
import com.sigpwned.litecene.core.query.TextQuery;
import com.sigpwned.litecene.core.query.VacuousQuery;
import com.sigpwned.litecene.core.util.QueryProcessor;
import com.sigpwned.litecene.test.Corpus;
import com.sigpwned.litecene.test.CorpusMatcher;
import com.sigpwned.litecene.test.Document;

/**
 * This is a toy in-memory matcher. It assumes that we're only searching lowercase printable ASCII
 * alphanumeric characters. It's mostly used to test the test suite.
 */
public class ExampleCorpusMatcher implements CorpusMatcher {
  @Override
  public Set<String> match(Corpus corpus, Query query) {
    return match2(Corpus.of(corpus.getDocuments().stream()
        .map(d -> Document.of(d.getId(), preprocess(d.getText()))).collect(toList())), query);
  }

  protected static Corpus preprocess(Corpus corpus) {
    return Corpus.of(corpus.getDocuments().stream()
        .map(d -> Document.of(d.getId(), preprocess(d.getText()))).collect(toList()));
  }

  protected static Document preprocess(Document document) {
    return Document.of(document.getId(), preprocess(document.getText()));
  }

  private static final Pattern MARKS = Pattern.compile("\\p{M}");

  private static final Pattern TOKEN = Pattern.compile("\\w+");

  protected static String preprocess(String text) {
    String lower = text.toLowerCase();
    String nfkd = Normalizer.normalize(lower, Normalizer.Form.NFKD);
    String unmarked = MARKS.matcher(nfkd).replaceAll("");
    return TOKEN.matcher(unmarked).results().map(MatchResult::group).collect(joining(" "));
  }

  protected Set<String> match2(Corpus corpus, Query query) {
    return new QueryProcessor<Set<String>>(new QueryProcessor.Processor<Set<String>>() {
      @Override
      public Set<String> and(AndQuery and) {
        if (and.getChildren().isEmpty())
          return emptySet();

        Set<String> ids = corpus.getDocuments().stream().map(Document::getId)
            .collect(Collectors.toCollection(HashSet::new));

        for (Query child : and.getChildren())
          ids.retainAll(match2(corpus, child));

        return ids;
      }

      @Override
      public Set<String> or(OrQuery or) {
        if (or.getChildren().isEmpty())
          return emptySet();

        Set<String> ids = new HashSet<>();
        for (Query child : or.getChildren())
          ids.addAll(match2(corpus, child));

        return ids;
      }

      @Override
      public Set<String> not(NotQuery not) {
        Set<String> ids = corpus.getDocuments().stream().map(Document::getId)
            .collect(Collectors.toCollection(HashSet::new));

        ids.removeAll(match2(corpus, not));

        return ids;
      }

      @Override
      public Set<String> list(ListQuery list) {
        if (list.getChildren().isEmpty())
          return emptySet();

        Set<String> ids = corpus.getDocuments().stream().map(Document::getId)
            .collect(Collectors.toCollection(HashSet::new));

        for (Query child : list.getChildren())
          ids.retainAll(match2(corpus, child));

        return ids;
      }

      @Override
      public Set<String> paren(ParenQuery paren) {
        return match2(corpus, paren.getChild());
      }

      @Override
      public Set<String> text(TextQuery text) {
        if (text.getProximity().isPresent()) {
          Set<String> result = new HashSet<>();
          documents: for (Document doc : corpus.getDocuments()) {
            // These are the tokens in our document string
            String doctext = TOKEN.matcher(doc.getText()).results().map(MatchResult::group)
                .collect(joining(" "));

            // Create a map of index in doctext to token position for proximity
            AtomicInteger position = new AtomicInteger(0);
            Map<Integer, Integer> indexToPosition = new HashMap<>();
            TOKEN.matcher(doctext).results().sequential().forEach(m -> {
              indexToPosition.put(m.start(), position.getAndIncrement());
            });

            // For each term in our query string, these are the indexes of the tokens that match
            List<List<Integer>> matches = text.getTerms().stream().map(t -> {
              // Build a list of matching token positions for each term
              return compile(t).matcher(doctext).results().map(m -> indexToPosition.get(m.start()))
                  .collect(toList());
            }).collect(toList());

            // If any permutation of matches fits in the given proximity, then this document
            // matches. Add its ID to the results and move onto the next document. Otherwise, we'll
            // just fall through, and the ID won't get added. Note that if any of the lists are
            // empty, then we will never match by definition of the cartesian product.
            for (List<Integer> permutation : Lists.cartesianProduct(matches)) {
              int min = permutation.stream().mapToInt(Integer::intValue).min()
                  .orElseThrow(AssertionError::new);
              int max = permutation.stream().mapToInt(Integer::intValue).max()
                  .orElseThrow(AssertionError::new);
              if (max - min + 1 <= text.getProximity().getAsInt()) {
                result.add(doc.getId());
                continue documents;
              }
            }
          }
          return result;
        } else {
          Pattern p = Pattern.compile(text.getTerms().stream().map(this::compile)
              .map(Pattern::pattern).collect(joining(" ")), Pattern.CASE_INSENSITIVE);
          return corpus.getDocuments().stream().filter(d -> p.matcher(d.getText()).find())
              .map(Document::getId).collect(toSet());
        }
      }

      private Pattern compile(Term term) {
        return compile(term.getText(), term.isWildcard());
      }

      private Pattern compile(String text, boolean wildcard) {
        StringBuilder pattern = new StringBuilder().append("\\b\\Q").append(text).append("\\E");
        if (wildcard)
          pattern.append("[a-z0-9]*");
        pattern.append("\\b");
        return Pattern.compile(pattern.toString(), Pattern.CASE_INSENSITIVE);
      }

      @Override
      public Set<String> vacuous(VacuousQuery vacuous) {
        // By definition
        return emptySet();
      }
    }).process(query);
  }
}
