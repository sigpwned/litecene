package com.sigpwned.litecene;

import java.util.Objects;

public class ParenQuery extends Query {
  private final Query child;

  public ParenQuery(Query child) {
    this.child = child;
  }

  /**
   * @return the child
   */
  public Query getChild() {
    return child;
  }

  @Override
  public int hashCode() {
    return Objects.hash(child);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ParenQuery other = (ParenQuery) obj;
    return Objects.equals(child, other.child);
  }

  @Override
  public String toString() {
    return "(" + getChild().toString() + ")";
  }
}
