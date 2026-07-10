// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Loc Ha
 *
 */
public interface AuthTokenManager {

  void save(AuthToken authToken);

  AuthToken load(UUID series);

  void remove(UUID series);

  List<AuthToken> query(LocalDate issuedStart, LocalDate issuedEnd);
}
