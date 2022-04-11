package com.sigpwned.litecene;

import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class OrQuery extends Query {
  private final List<Query> children;

  public OrQuery(List<Query> children) {
    if (children.size() < 2)
      throw new IllegalArgumentException("not enough children");
    this.children = unmodifiableList(children);
  }

  /**
   * @return the children
   */
  public List<Query> getChildren() {
    return children;
  }

  @Override
  public int hashCode() {
    return Objects.hash(children);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OrQuery other = (OrQuery) obj;
    return Objects.equals(children, other.children);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    Iterator<Query> iterator = getChildren().iterator();
    result.append(iterator.next().toString());
    while (iterator.hasNext()) {
      result.append(" OR ");
      result.append(iterator.next().toString());
    }

    return result.toString();
  }
}
