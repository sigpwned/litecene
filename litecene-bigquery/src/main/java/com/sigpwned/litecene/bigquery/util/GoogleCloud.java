/*-
 * =================================LICENSE_START==================================
 * litecene-bigquery
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
package com.sigpwned.litecene.bigquery.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;

public final class GoogleCloud {
  private GoogleCloud() {}

  /**
   * The name of the environment variable to check for credentials
   */
  public static final String CREDENTIAL_ENV_NAME = ServiceOptions.CREDENTIAL_ENV_NAME;

  /**
   * The default file to check for credentials
   */
  public static final File DEFAULT_CREDENTIALS_FILE =
      new File(new File(new File(System.getProperty("user.home")), ".gcp"), "credentials.json");

  /**
   * Attempts to load credentials from the environment in the following ways:
   * 
   * - If environment variable CREDENTIAL_ENV_NAME contains JSON, then read credentials from the
   * contents of the environment variable
   * 
   * - If environment variable CREDENTIAL_ENV_NAME starts with "https:" or "file:", then treat the
   * contents of the environment variable as URL and try to open a stream to it and load credentials
   * from there
   * 
   * - Otherwise, if CREDENTIAL_ENV_NAME is set, treat it as a filepath, which is the default GCP
   * behavior.
   * 
   * - If CREDENTIAL_ENV_NAME is not set, then check $HOME/.gcp/credentials.json.
   * 
   * Returns null if credentials cannot be found.
   * 
   * @throws UncheckedIOException if an error occurs while attempting to read data
   */
  public static GoogleCredentials getDefaultCredentials() {
    try {
      if (System.getenv(CREDENTIAL_ENV_NAME) != null) {
        String text = System.getenv(CREDENTIAL_ENV_NAME);
        if (text.startsWith("{")) {
          // This is JSON stuffed into an environment variable.
          try (InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))) {
            return GoogleCredentials.fromStream(in);
          }
        } else if (text.startsWith("https:") || text.startsWith("file:")) {
          // This is a URL. Is it secure? That's the user's problem!
          try (InputStream in = new URL(text).openStream()) {
            return GoogleCredentials.fromStream(in);
          }
        } else {
          // Let's just assume this is a filename
          try (InputStream in = new FileInputStream(text)) {
            return GoogleCredentials.fromStream(in);
          }
        }
      } else if (DEFAULT_CREDENTIALS_FILE.isFile()) {
        try (InputStream in = new FileInputStream(DEFAULT_CREDENTIALS_FILE)) {
          return GoogleCredentials.fromStream(in);
        }
      } else {
        return null;
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read GCP credentials", e);
    }
  }
}
