package com.sigpwned.litecene;

import java.util.Objects;

public class NotQuery extends Query {
  private final Query child;

  public NotQuery(Query child) {
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
    NotQuery other = (NotQuery) obj;
    return Objects.equals(child, other.child);
  }

  @Override
  public String toString() {
    return "NOT " + getChild().toString();
  }
}
