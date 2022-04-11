package com.sigpwned.litecene;

import java.util.Objects;

public class TermQuery extends Query {
  private final String text;

  public TermQuery(String text) {
    this.text = text;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TermQuery other = (TermQuery) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return getText();
  }
}
