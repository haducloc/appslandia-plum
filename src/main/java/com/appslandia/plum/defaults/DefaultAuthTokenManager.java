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
import com.appslandia.plum.base.AuthToken;
import com.appslandia.plum.base.AuthTokenManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAuthTokenManager implements AuthTokenManager {

  final Map<UUID, AuthToken> tokenMap = new LruMap<>(100);

  @Override
  public synchronized void save(AuthToken authToken) {
    if (authToken.getSeries() == null) {
      authToken.setSeries(UUID.randomUUID());
    }
    tokenMap.put(authToken.getSeries(), copy(authToken));
  }

  @Override
  public synchronized AuthToken load(UUID series) {
    var authToken = tokenMap.get(series);
    if (authToken == null) {
      return null;
    }
    return copy(authToken);
  }

  @Override
  public synchronized void remove(UUID series) {
    tokenMap.remove(series);
  }

  @Override
  public synchronized List<AuthToken> query(LocalDate issuedStart, LocalDate issuedEnd) {
    var issuedStartUtc = (issuedStart != null) ? issuedStart.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    var issuedEndUtc = (issuedEnd != null) ? issuedEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

    // @formatter:off
    return tokenMap.values().stream()
        .filter(e ->
                  ((issuedStartUtc == null) || (e.getIssuedAtUtc().compareTo(issuedStartUtc) >= 0)) &&
                  ((issuedEndUtc == null) || (e.getIssuedAtUtc().compareTo(issuedEndUtc) < 0))
              )
        .sorted(Comparator.comparing(AuthToken::getIssuedAtUtc).reversed())
        .map(e -> copy(e))
        .toList();
    // @formatter:on
  }

  static AuthToken copy(AuthToken obj) {
    var token = new AuthToken();
    token.setSeries(obj.getSeries());
    token.setHashToken(obj.getHashToken());
    token.setIdentity(obj.getIdentity());
    token.setModule(obj.getModule());

    token.setIssuedAtUtc(obj.getIssuedAtUtc());
    token.setExpiresAtUtc(obj.getExpiresAtUtc());
    return token;
  }
}
