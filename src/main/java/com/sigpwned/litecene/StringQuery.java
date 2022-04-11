package com.sigpwned.litecene;

import java.util.Objects;
import java.util.OptionalInt;

public class StringQuery extends Query {
  private final String text;
  private final Integer proximity;

  public StringQuery(String text, Integer proximity) {
    this.text = text;
    this.proximity = proximity;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the proximity
   */
  public OptionalInt getProximity() {
    return proximity != null ? OptionalInt.of(proximity.intValue()) : OptionalInt.empty();
  }

  @Override
  public int hashCode() {
    return Objects.hash(proximity, text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringQuery other = (StringQuery) obj;
    return Objects.equals(proximity, other.proximity) && Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder().append("\"").append(getText()).append("\"");
    if (proximity != null) {
      result.append("~").append(proximity);
    }
    return result.toString();
  }
}
