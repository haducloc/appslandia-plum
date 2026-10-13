// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.plum.oauth2.OAuth2State;
import com.appslandia.plum.oauth2.OAuth2StateManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultOAuth2StateManager implements OAuth2StateManager {

  final Map<UUID, OAuth2State> stateMap = new LruMap<>(100);

  @Override
  public synchronized void insert(OAuth2State state) {
    stateMap.put(state.getSeries(), copy(state));
  }

  @Override
  public synchronized OAuth2State load(UUID series) {
    var state = stateMap.get(series);
    if (state == null) {
      return null;
    }
    return copy(state);
  }

  @Override
  public synchronized void remove(UUID series) {
    stateMap.remove(series);
  }

  @Override
  public synchronized List<OAuth2State> query(LocalDate issuedStart, LocalDate issuedEnd) {
    var issuedStartUtc = (issuedStart != null) ? issuedStart.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    var issuedEndUtc = (issuedEnd != null) ? issuedEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

    // @formatter:off
    return stateMap.values().stream()
        .filter(e ->
                  ((issuedStartUtc == null) || (e.getIssuedAtUtc().compareTo(issuedStartUtc) >= 0)) &&
                  ((issuedEndUtc == null) || (e.getIssuedAtUtc().compareTo(issuedEndUtc) < 0))
              )
        .sorted(Comparator.comparing(OAuth2State::getIssuedAtUtc).reversed())
        .map(e -> copy(e))
        .toList();
    // @formatter:on
  }

  static OAuth2State copy(OAuth2State state) {
    var obj = new OAuth2State();
    obj.setSeries(state.getSeries());
    obj.setModule(state.getModule());
    obj.setProvider(state.getProvider());
    obj.setReturnUrl(state.getReturnUrl());
    obj.setCodeVerifier(state.getCodeVerifier());

    obj.setIntegrityHash(state.getIntegrityHash());
    obj.setIssuedAtUtc(state.getIssuedAtUtc());
    obj.setExpiresAtUtc(state.getExpiresAtUtc());
    return obj;
  }
}
