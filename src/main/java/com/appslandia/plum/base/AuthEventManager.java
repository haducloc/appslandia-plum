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
public interface AuthEventManager {

  void save(AuthEvent event);

  AuthEvent load(UUID authEventId);

  List<AuthEvent> query(LocalDate authStart, LocalDate authEnd);
}
