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
import com.appslandia.plum.base.RemMeToken;
import com.appslandia.plum.base.RemMeTokenManager;

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
  public synchronized void save(RemMeToken remMeToken) {
    if (remMeToken.getSeries() == null) {
      remMeToken.setSeries(UUID.randomUUID());
    }
    tokenMap.put(remMeToken.getSeries(), copy(remMeToken));
  }

  @Override
  public synchronized RemMeToken load(UUID series) {
    var remMeToken = tokenMap.get(series);
    if (remMeToken == null) {
      return null;
    }
    return copy(remMeToken);
  }

  @Override
  public synchronized void update(UUID series, String hashToken, Instant issuedAtUtc, Instant expiresAtUtc) {
    var obj = tokenMap.get(series);
    Arguments.notNull(obj);

    obj.setHashToken(hashToken);
    obj.setIssuedAtUtc(issuedAtUtc);
    obj.setExpiresAtUtc(expiresAtUtc);
  }

  @Override
  public synchronized void remove(UUID series) {
    tokenMap.remove(series);
  }

  @Override
  public synchronized void removeAll(String identity) {
    tokenMap.entrySet().removeIf(e -> e.getValue().getIdentity().equals(identity));
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

  static RemMeToken copy(RemMeToken obj) {
    var token = new RemMeToken();
    token.setSeries(obj.getSeries());
    token.setHashToken(obj.getHashToken());
    token.setIdentity(obj.getIdentity());
    token.setModule(obj.getModule());

    token.setIssuedAtUtc(obj.getIssuedAtUtc());
    token.setExpiresAtUtc(obj.getExpiresAtUtc());
    return token;
  }
}
