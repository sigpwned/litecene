/*-
 * =================================LICENSE_START==================================
 * litecene-core
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
