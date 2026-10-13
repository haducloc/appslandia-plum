// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Loc Ha
 *
 */
public interface RemMeTokenManager {

  void insert(RemMeToken token);

  RemMeToken load(UUID series);

  void update(UUID series, String tokenHash, String integrityHash, Instant issuedAtUtc, Instant expiresAtUtc);

  void remove(UUID series);

  void removeAll(String identity);

  List<RemMeToken> query(LocalDate issuedStart, LocalDate issuedEnd);
}
