package com.sigpwned.litecene.core.query.token;

import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import com.sigpwned.litecene.core.Term;
import com.sigpwned.litecene.core.Token;

public class TextToken extends Token {
  private final List<Term> terms;
  private final Integer proximity;

  public TextToken(List<Term> terms, OptionalInt proximity) {
    this(terms, proximity.isPresent() ? Integer.valueOf(proximity.getAsInt()) : null);
  }

  public TextToken(List<Term> terms, Integer proximity) {
    super(Token.Type.TEXT);
    if (terms == null)
      throw new NullPointerException();
    if (terms.isEmpty() && proximity != null)
      throw new IllegalArgumentException("proximity with empty terms");
    if (proximity != null && proximity <= 0)
      throw new IllegalArgumentException("proximity must be positive");
    if (proximity != null && proximity < terms.size())
      throw new IllegalArgumentException("proximity too small for terms");
    this.terms = unmodifiableList(terms);
    this.proximity = proximity;
  }

  public OptionalInt getProximity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  /**
   * @return the terms
   */
  public List<Term> getTerms() {
    return terms;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(proximity, terms);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TextToken other = (TextToken) obj;
    return Objects.equals(proximity, other.proximity) && Objects.equals(terms, other.terms);
  }

  @Override
  public String toString() {
    final int maxLen = 10;
    return "TextToken [terms="
        + (terms != null ? terms.subList(0, Math.min(terms.size(), maxLen)) : null) + ", proximity="
        + proximity + "]";
  }
}
