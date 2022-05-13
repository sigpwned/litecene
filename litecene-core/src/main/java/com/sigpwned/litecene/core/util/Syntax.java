package com.sigpwned.litecene.core.util;

import java.util.regex.Pattern;

public final class Syntax {
  private Syntax() {}

  /**
   * The double quote metacharacter starts and ends phrases
   */
  public static final int QUOTE = '"';

  /**
   * The tilde metacharacter starts proximity right after the end of a phrase
   */
  public static final int TILDE = '~';

  /**
   * The left parenthesis metacharacter opens a query group
   */
  public static final int LPAREN = '(';

  /**
   * The left parenthesis metacharacter closes a query group
   */
  public static final int RPAREN = ')';

  /**
   * The star metacharacter indicates a wildcard prefix
   */
  public static final int STAR = '*';

  /**
   * The AND keyword is the boolean "and" operator
   */
  public static final String AND = "AND";

  /**
   * The OR keyword is the boolean "or" operator
   */
  public static final String OR = "OR";

  /**
   * The NOT keyword is the boolean "not" operator
   */
  public static final String NOT = "NOT";

  /**
   * One or more contiguous whitespace characters
   */
  public static final Pattern WHITESPACE = Pattern.compile("\\p{javaWhitespace}+");
}
