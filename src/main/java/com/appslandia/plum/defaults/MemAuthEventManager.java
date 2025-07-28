// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.defaults;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.plum.base.AuthEvent;
import com.appslandia.plum.base.AuthEventManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class MemAuthEventManager implements AuthEventManager {

  final Map<UUID, AuthEvent> eventMap = Collections.synchronizedMap(new LruMap<>(100));

  @Override
  public void save(AuthEvent event) {
    event.setAuthEventId(UUID.randomUUID());
    this.eventMap.put(event.getAuthEventId(), event);
  }

  @Override
  public AuthEvent load(UUID authEventId) {
    var event = this.eventMap.get(authEventId);
    if (event == null) {
      return null;
    }
    return copy(event);
  }

  @Override
  public List<AuthEvent> query(LocalDate authStart, LocalDate authEnd) {
    var authStartUtc = DateUtils.atStartOfDay(authStart);
    var authEndUtc = DateUtils.atEndOfDay(authEnd);

    return this.eventMap.values().stream()
        .filter(e -> ((authStartUtc == null) || (e.getAuthAtUtc().compareTo(authStartUtc) >= 0))
            && ((authEndUtc == null) || (e.getAuthAtUtc().compareTo(authEndUtc) <= 0)))
        .sorted((t1, t2) -> t2.getAuthAtUtc().compareTo(t1.getAuthAtUtc())).map(e -> copy(e)).toList();
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
    event.setRemmeSeries(obj.getRemmeSeries());
    event.setAuthAtUtc(obj.getAuthAtUtc());

    return event;
  }
}
