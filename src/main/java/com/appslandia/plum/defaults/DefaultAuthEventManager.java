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
import com.appslandia.plum.base.AuthEvent;
import com.appslandia.plum.base.AuthEventManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAuthEventManager implements AuthEventManager {

  final Map<UUID, AuthEvent> eventMap = new LruMap<>(100);

  @Override
  public synchronized void save(AuthEvent event) {
    event.setAuthEventId(UUID.randomUUID());
    eventMap.put(event.getAuthEventId(), copy(event));
  }

  @Override
  public synchronized AuthEvent load(UUID authEventId) {
    var event = eventMap.get(authEventId);
    if (event == null) {
      return null;
    }
    return copy(event);
  }

  @Override
  public synchronized List<AuthEvent> query(LocalDate authStart, LocalDate authEnd) {
    var authStartUtc = (authStart != null) ? authStart.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    var authEndUtc = (authEnd != null) ? authEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

    // @formatter:off
    return eventMap.values().stream()
        .filter(e ->
                  ((authStartUtc == null) || (e.getAuthAtUtc().compareTo(authStartUtc) >= 0)) &&
                  ((authEndUtc == null) || (e.getAuthAtUtc().compareTo(authEndUtc) < 0))
              )
        .sorted(Comparator.comparing(AuthEvent::getAuthAtUtc).reversed())
        .map(e -> copy(e))
        .toList();
    // @formatter:on
  }

  static AuthEvent copy(AuthEvent obj) {
    var event = new AuthEvent();

    event.setAuthEventId(obj.getAuthEventId());
    event.setIdentity(obj.getIdentity());
    event.setModule(obj.getModule());
    event.setAuthType(obj.getAuthType());
    event.setAuthResult(obj.getAuthResult());

    event.setClientIp(obj.getClientIp());
    event.setUserAgent(obj.getUserAgent());
    event.setSeries(obj.getSeries());
    event.setAuthAtUtc(obj.getAuthAtUtc());
    event.setCallerUniqueId(obj.getCallerUniqueId());

    event.setSessionId(obj.getSessionId());
    event.setRequestId(obj.getRequestId());

    return event;
  }
}
