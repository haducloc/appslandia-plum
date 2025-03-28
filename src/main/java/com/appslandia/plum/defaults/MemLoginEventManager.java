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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.plum.base.LoginEvent;
import com.appslandia.plum.base.LoginEventManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class MemLoginEventManager implements LoginEventManager {

  final Map<UUID, LoginEvent> eventMap = Collections.synchronizedMap(new LruMap<>(100));

  @Override
  public void save(LoginEvent event) {
    event.setEventId(UUID.randomUUID());
    this.eventMap.put(event.getEventId(), event);
  }

  @Override
  public LoginEvent load(UUID loginEventId) {
    LoginEvent event = this.eventMap.get(loginEventId);
    if (event == null) {
      return null;
    }
    return copy(event);
  }

  @Override
  public List<LoginEvent> query(LocalDate loginStart, LocalDate loginEnd) {
    LocalDateTime loginStartUtc = (loginStart != null) ? loginStart.atTime(LocalTime.MIN) : null;
    LocalDateTime loginEndUtc = (loginEnd != null) ? loginEnd.atTime(LocalTime.MAX) : null;

    return this.eventMap.values().stream()
        .filter(e -> ((loginStartUtc == null) || (e.getLoginAtUtc().compareTo(loginStartUtc) >= 0))
            && ((loginEndUtc == null) || (e.getLoginAtUtc().compareTo(loginEndUtc) <= 0)))
        .sorted((t1, t2) -> t2.getLoginAtUtc().compareTo(t1.getLoginAtUtc())).map(e -> copy(e)).toList();
  }

  static LoginEvent copy(LoginEvent obj) {
    LoginEvent event = new LoginEvent();

    event.setEventId(obj.getEventId());
    event.setIdentity(obj.getIdentity());
    event.setModule(obj.getModule());
    event.setEventType(obj.getEventType());
    event.setLoginResult(obj.getLoginResult());

    event.setClientIp(obj.getClientIp());
    event.setUserAgent(obj.getUserAgent());
    event.setSeries(obj.getSeries());
    event.setLoginAtUtc(obj.getLoginAtUtc());

    return event;
  }
}
