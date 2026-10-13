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
import com.appslandia.common.utils.Arguments;
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
  public synchronized void insert(AuthToken token) {
    tokenMap.put(token.getSeries(), copy(token));
  }

  @Override
  public synchronized AuthToken load(UUID series) {
    var token = tokenMap.get(series);
    if (token == null) {
      return null;
    }
    return copy(token);
  }

  @Override
  public synchronized void updateCodeAttempts(UUID series, int codeAttempts) {
    var token = tokenMap.get(series);
    Arguments.notNull(token);

    token.setCodeAttempts(codeAttempts);
  }

  @Override
  public synchronized void updateCodeHash(UUID series, String codeHash) {
    var token = tokenMap.get(series);
    Arguments.notNull(token);

    token.setCodeHash(codeHash);
  }

  @Override
  public synchronized void remove(UUID series) {
    tokenMap.remove(series);
  }

  @Override
  public synchronized void removeAll(String identity) {
    Arguments.notNull(identity);
    tokenMap.entrySet().removeIf(e -> identity.equalsIgnoreCase(e.getValue().getIdentity()));
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

  static AuthToken copy(AuthToken token) {
    var obj = new AuthToken();
    obj.setSeries(token.getSeries());
    obj.setModule(token.getModule());
    obj.setIdentity(token.getIdentity());
    obj.setData(token.getData());

    obj.setCodeHash(token.getCodeHash());
    obj.setCodeAttempts(token.getCodeAttempts());
    obj.setMaxAttempts(token.getMaxAttempts());

    obj.setIntegrityHash(token.getIntegrityHash());
    obj.setIssuedAtUtc(token.getIssuedAtUtc());
    obj.setExpiresAtUtc(token.getExpiresAtUtc());
    return obj;
  }
}
