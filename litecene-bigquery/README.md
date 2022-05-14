# LITECENE BIGQUERY

This module provides full-text search support using the litecene boolean query syntax to BigQuery.

## Features

This module supports the following full-text search features:

* Full litecene syntax support
* BigQuery search index integration
* English, French, Spanish, German, Italian, Portuguese, and other Latin languages (searches 7-bit ASCII after Unicode NFKD normalization and mark removal)

This module does not support the following full-text search features:

* Match scoring
* Non-latin languages (CJK, Russian, Arabic, Hindi, Hebrew, etc.)

## Preparation

The BigQuery field to be searched must be "analyzed" using the following expression (or equivalent):

    LOWER(TRIM(REGEXP_REPLACE(REGEXP_REPLACE(NORMALIZE(field, NFKD), r"\p{M}", ''), r"[^a-zA-Z0-9]+", ' ')))

You can find this expression in [BigQuerySearching#recommendedAnalysisExpr](https://github.com/sigpwned/litecene/blob/main/litecene-bigquery/src/main/java/com/sigpwned/litecene/bigquery/util/BigQuerySearching.java#L60).

### Using Materialized Views

One convenient way to do this is using materialized views, now that BigQuery supports non-aggregate views. For a table `example.table` with the following schema:

    id    STRING
    text  STRING

The following query materialized view can be used to create a materialized view `example.searchable` that prepares the `text` field for search and stores the result in the `analyzed` field:

    CREATE MATERIALIZED VIEW example.searchable
    AS
    SELECT
      id,
      text,
      LOWER(TRIM(REGEXP_REPLACE(REGEXP_REPLACE(NORMALIZE(text, NFKD), r"\p{M}", ''), r"[^a-zA-Z0-9]+", ' '))) AS analyzed
    FROM
      `example.table`

This approach will incur additional cost, but for most datasets, the cost will be negligible.

### Other Approaches

Of course, there are other approaches, too, such as:

* Analyze the text going into the database using application code and store as a separate field
* Create a traditional non-materialized view, although this will affect performance bcause text is analyzed at query time for each query.

## Example

Given the above example, a query can be created like this:

    System.out.println(
      String.format("SELECT id, text FROM `example.searchable` t WHERE (%s) ORDER BY id ASC LIMIT 10 OFFSET 0",
        new BigQuerySearchCompiler("t.analyzed").compile(BigQuerySearching.recommendedParseQuery("hello OR world"))));

    SELECT id, text FROM `example.searchable` t WHERE (((REGEXP_CONTAINS(t.analyzed, r"\b\Qhello\E\b")) OR (REGEXP_CONTAINS(t.analyzed, r"\b\Qworld\E\b")))) ORDER BY id ASC LIMIT 10 OFFSET 0

Since the result is just SQL, the user then runs the query like normal using [the BigQuery SDK](https://cloud.google.com/bigquery/docs/reference/libraries#client-libraries-install-java).

## Using BigQuery Search Indexes

BigQuery is (finally) adding [features to support full-text search natively](https://cloud.google.com/bigquery/docs/release-notes#1d2af2db), namely using [search functions](https://cloud.google.com/bigquery/docs/reference/standard-sql/search_functions) with [search indexes](https://cloud.google.com/bigquery/docs/search-index). However, at the time of this writing, these features are fairly immature, supporting only multiterm "and" logic. That said, this module supports the `SEARCH` function and will use it whenever possible if the user indicates that the searched field is indexed:

    System.out.println(
      String.format("SELECT id, text FROM `example.searchable` t WHERE (%s) ORDER BY id ASC LIMIT 10 OFFSET 0",
        new BigQuerySearchCompiler("t.analyzed", true).compile(BigQuerySearching.recommendedParseQuery("\"hello world\""))));
    
    SELECT id, text FROM `example.searchable` t WHERE ((SEARCH(c.text, 'hello world')) AND (REGEXP_CONTAINS(c.text, r"\b\Qhello\E\b \b\Qworld\E\b"))) ORDER BY id ASC LIMIT 10 OFFSET 0

Note the new second argument to the `BigQuerySearchCompiler`.

The `SEARCH` function is used if litecene detects that some non-wildcard tokens must always appear in a document for the document to match the query and regular expressions are used to implement the remaining query logic. If a query can be expressed entirely in these terms, then searching will be done entirely using the `SEARCH` function without using regular expressions at all:

    System.out.println(
      String.format("SELECT id, text FROM `example.searchable` t WHERE (%s) ORDER BY id ASC LIMIT 10 OFFSET 0",
        new BigQuerySearchCompiler("t.analyzed", true).compile(BigQuerySearching.recommendedParseQuery("hello world"))));
    
    SELECT id, text FROM `example.searchable` t WHERE ((SEARCH(c.text, 'hello world')) ORDER BY id ASC LIMIT 10 OFFSET 0

