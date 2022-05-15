# LITECENE [![tests](https://github.com/sigpwned/litecene/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/litecene/actions/workflows/tests.yml) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_litecene&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_litecene) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_litecene&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_litecene) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_litecene&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_litecene)

A simple cross-data store full-text search language implemented for Java 8+

## Motivation

Full-text search is a key feature of modern applications. However, different data stores expose different syntaxes for performing full-text search, and many of these syntaxes are not user-friendly. Some data stores don't expose any full-text search features at all! This makes it difficult for developers to expose a consistent user-facing search experience. Litecene is a familiar standard query language based on [Lucene syntax](https://lucene.apache.org/core/2_9_4/queryparsersyntax.html) with transpilers to popular data stores like BigQuery that makes it easy for developers to expose a consistent, user-friendly search syntax to users while making the most of their application data store's full-text search features.

## Goals

* Define a well-documented, user-friendly query syntax that can be transpiled to a broad range of data stores
* Provide transpilers for popular data stores out-of-the-box
* Allow developers to create transpilers of their own to support data stores without out-of-the-box support

## Non-Goals

* Identical search behavior across all data stores (This is not possible when different data stores make different decisions about their full-text search features)
* Support all languages across all data stores (Language support is limited by data backend feature sets)
* Support all data stores
* Support search of non-text fields (e.g., integers, dates)

## Query Syntax

This section describes the common Litecene query syntax. Note that different transpilers may implement text search for the same query differently. This section merely describes what constitutes a valid query and the logical definition of a match; each transpiler implementation documents exactly how valid queries are matched in the associated data store.

As an example, this might be a good Litecene query to identify social media posts mentioning common ways people user their smartphones:

    (smartphone OR "smart phone" OR iphone OR "apple phone" OR android OR "google phone" OR "windows phone" OR "phone app"~8) AND (call OR dial OR app OR surf OR browse OR camera OR picture OR pic OR selfie)

Litecene supports seven search clause types: term, phrase, list, groups, and, or, and not.

### Term Clause

#### Standard Term Clause

A term clause is an unquoted string of non-whitespace characters. A matching document must contain the given term. The following are all valid Litecene term clauses:

* `hello`
* `world`
* `what's`
* `#selfie`
* `@twitter`
* `https://www.example.com/this/is/a/hyperlink`

#### Prefix Term Clause

A term clause may also end with a wildcard (`*`) to indicate a prefix search. In this case, a matching document must contain a term with the given prefix. The following are all valid Litecene prefix term clauses:

* `developer*`
* `what's*`
* `https://www.example.com/*`

### Phrase Clause

#### Standard Phrase Clause

A phrase clause is a quoted string of characters. A matching document must contain the given terms next to each other. The following are all valid Litecene phrase clauses:

* `"hello, world!"`
* `"The rain in Spain falls mainly on the plains."`
* `"super cool search"`

#### Wilcard Terms in Phrase Clause

A term in a phrase clause can end with a wildcard (`*`) to indicate a prefix search. In this case, a matching document must contain the given terms with the given prefixes next to each other. The following are all valid Litecene phrase clauses with wildcard terms:

* `"It wa* the best of times"`
* `"It was the wors* of times"`

#### Proximity Phrase Clause

A phrase clause can be followed immediately by a tilde (`~`) and an integer number to indicate a proximity search. In this case, a matching document must contain all the given regular or prefix terms within the given number of terms of each other in any order. The proximity length must be at least the number of terms in the phrase. The following are valid Litecene phrase clauses with proximity:

* `"hello, world!"~8`
* `"It wa* the best of times"~10`

### List Clause

A list clause is two or more valid Litecene search clauses separated by whitespace. A matching document must match the given clauses in any order. The following are all valid Litecene phrase clauses:

* `hello, world!`
* `The rain in Spain falls mainly on the plains.`
* `engineer* "developer* productivity"~8`

### Group Clause

A group clause is any other valid Litcene search clause surrounded by parentheses `(` `)`. A matching document must match the contained clause. The following are all valid Litecene group clauses:

* `(hello world)`
* `(engineer* "developer* productivity"~10)`

Group clauses are used mostly to clarify complex queries containing multiple and and or clauses.

### And Clause

An and clause is a valid Litecene search clause followed by the keyword `AND` followed by another Litecene search clause. Multiple `AND` clauses can be appended to the same and clause. A matching document must match all of the given clauses. The following are all valid Litecene and clauses:

* `hello AND world`
* `The rain in Spain falls mainly on the plains AND "My Fair Lady"~8`
* `engineer* AND "developer productivity"~10`

### Or Clause

An or clause is a valid Litecene search clause followed by the keyword `OR` followed by another Litecene search clause. Multiple `OR` clauses can be appended to the same or clause. When and and or clauses are interleaved, the `AND` operator binds tighter. A group clause can be used to make a query clearer and easier to understand. A matching document must match at least one of the given clauses. The following are all valid Litecene or clauses:

* `hello OR world`
* "My Fair Lady"~8 OR Pygmalion`

### Not Clause

A not clause is the `NOT` keyword followed by another Litecene search clause. A matching document must *not* match the given clause. The following are all valid Litcene not clauses:

* `NOT hello`
* `NOT "hello, world!"`
* `NOT (engineer* AND "developer productivity"~10)`

Some query backends do not allow a query that contains only `NOT` clauses.

## Example Usage

For examples of how to use Litecene in your application, refer to the README for your application's data store.

## Building a New Data Store Integration

A new data store integration has two parts: a query transpiler that converts a Litecene `Query` object into the data store's native syntax; and a method to prepare text in the data store for searching. These two components must work together to implement correct search semantics.

For example, the BigQuery data store integration only searches ASCII latin letters and numbers. Therefore, the query must be postprocessed to handle non-searched characters appropriately before it can be converted into a SQL predicate, and the data in the data store must be preprocessed to eliminate non-searched characters, and both of these processed must be done such that they agree with each other.

A new data integration backend must implement a helper to construct a suggested query analysis pipeline and a suggested approach to data preprocessing.

## Architecture

Litecene implements the following pipelines to allow data store integration developers to build complex processing rules quickly and easily.

### CodePointStream

Litecene views all query text as a sequence of [code points](https://en.wikipedia.org/wiki/Code_point). The `CodePointStream` is an ordered sequence of code points. Litecene core contains the following code point filters:

* `SmartQuotesCodePointFilter` -- Converts “smart quotes” to "straight quotes"

Code point filters can map one code point to another, but cannot add or remove code points.

### TokenStream

Litecene uses [tokenization](https://en.wikipedia.org/wiki/Lexical_analysis#Tokenization) to convert code points into tokens. The `TokenStream` is an ordered sequence of tokens. Litecene core contains the following token filters:

* `LetterNumberTokenFilter` -- Replaces all non-alphanumeric characters with whitespace. The Unicode [categories](https://en.wikipedia.org/wiki/Unicode_character_property#General_Category) Letter and Number are used to define alphanumeric text.
* `LowercaseTokenFilter` -- Converts all text to lowercase.
* `NormalizeTokenFilter` -- Performs [Unicode NFKD normalization](https://en.wikipedia.org/wiki/Unicode_equivalence#Normalization) on the text and removes all Unicode Mark characters.
* `PrintableAsciiTokenFilter` -- Replaces all characters not in `0x20-0x7E` with whitespace.

Token filters can make arbitrary changes to token text, but cannot add or remove tokens.

### QueryPipeline

Litecene allows for arbitrary query transformations using a `QueryPipeline`. Litecene core contains the following query pipelines:

* `SimplifyQueryFilterPipeline` -- Restructures a query to remove "vacuous" terms (e.g., terms with no text) and simplify logic (e.g., merge adjacent and queries)