// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.remme.RemMeToken;
import com.appslandia.plum.remme.RemMeTokenManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultRemMeTokenManager implements RemMeTokenManager {

  final Map<UUID, RemMeToken> tokenMap = new LruMap<>(100);

  @Override
  public synchronized void insert(RemMeToken token) {
    tokenMap.put(token.getSeries(), copy(token));
  }

  @Override
  public synchronized RemMeToken load(UUID series) {
    var token = tokenMap.get(series);
    if (token == null) {
      return null;
    }
    return copy(token);
  }

  @Override
  public synchronized void update(UUID series, String tokenHash, String integrityHash, Instant issuedAtUtc,
      Instant expiresAtUtc) {
    var token = tokenMap.get(series);
    Arguments.notNull(token);

    token.setTokenHash(tokenHash);
    token.setIntegrityHash(integrityHash);

    token.setIssuedAtUtc(issuedAtUtc);
    token.setExpiresAtUtc(expiresAtUtc);
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
  public synchronized List<RemMeToken> query(LocalDate issuedStart, LocalDate issuedEnd) {
    var issuedStartUtc = (issuedStart != null) ? issuedStart.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    var issuedEndUtc = (issuedEnd != null) ? issuedEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

    // @formatter:off
    return tokenMap.values().stream()
        .filter(e ->
                  ((issuedStartUtc == null) || (e.getIssuedAtUtc().compareTo(issuedStartUtc) >= 0)) &&
                  ((issuedEndUtc == null) || (e.getIssuedAtUtc().compareTo(issuedEndUtc) < 0))
              )
        .sorted(Comparator.comparing(RemMeToken::getIssuedAtUtc).reversed())
        .map(e -> copy(e))
        .toList();
    // @formatter:on
  }

  static RemMeToken copy(RemMeToken token) {
    var obj = new RemMeToken();
    obj.setSeries(token.getSeries());
    obj.setTokenHash(token.getTokenHash());

    obj.setModule(token.getModule());
    obj.setIdentity(token.getIdentity());

    obj.setIntegrityHash(token.getIntegrityHash());
    obj.setIssuedAtUtc(token.getIssuedAtUtc());
    obj.setExpiresAtUtc(token.getExpiresAtUtc());
    return obj;
  }
}
