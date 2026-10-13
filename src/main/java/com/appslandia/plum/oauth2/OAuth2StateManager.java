// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Loc Ha
 *
 */
public interface OAuth2StateManager {

  void insert(OAuth2State state);

  OAuth2State load(UUID series);

  void remove(UUID series);

  List<OAuth2State> query(LocalDate issuedStart, LocalDate issuedEnd);
}
