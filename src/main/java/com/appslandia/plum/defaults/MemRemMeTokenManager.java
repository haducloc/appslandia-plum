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
public class MemRemMeTokenManager implements RemMeTokenManager {

  final Map<UUID, RemMeToken> tokenMap = Collections.synchronizedMap(new LruMap<>(100));

  @Override
  public void save(RemMeToken remMeToken) {
    remMeToken.setSeries(UUID.randomUUID());
    this.tokenMap.put(remMeToken.getSeries(), copy(remMeToken));
  }

  @Override
  public RemMeToken load(UUID series) {
    var remMeToken = this.tokenMap.get(series);
    if (remMeToken == null) {
      return null;
    }
    return copy(remMeToken);
  }

  @Override
  public void update(UUID series, String hashToken, LocalDateTime issuedAtUtc, LocalDateTime expiresAtUtc) {
    var obj = this.tokenMap.get(series);
    Arguments.notNull(obj);

    obj.setHashToken(hashToken);
    obj.setIssuedAtUtc(issuedAtUtc);
    obj.setExpiresAtUtc(expiresAtUtc);
  }

  @Override
  public void remove(UUID series) {
    this.tokenMap.remove(series);
  }

  @Override
  public void removeAll(String identity) {
    this.tokenMap.entrySet().removeIf(e -> e.getValue().getIdentity().equals(identity));
  }

  @Override
  public List<RemMeToken> query(LocalDate issuedStart, LocalDate issuedEnd) {
    var issuedStartUtc = (issuedStart != null) ? issuedStart.atTime(LocalTime.MIN) : null;
    var issuedEndUtc = (issuedEnd != null) ? issuedEnd.atTime(LocalTime.MAX) : null;

    return this.tokenMap.values().stream()
        .filter(e -> ((issuedStartUtc == null) || (e.getIssuedAtUtc().compareTo(issuedStartUtc) >= 0))
            && ((issuedEndUtc == null) || (e.getIssuedAtUtc().compareTo(issuedEndUtc) <= 0)))
        .sorted((t1, t2) -> t2.getIssuedAtUtc().compareTo(t1.getIssuedAtUtc())).map(e -> copy(e)).toList();
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
