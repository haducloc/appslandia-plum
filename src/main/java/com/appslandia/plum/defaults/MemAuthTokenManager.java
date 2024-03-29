// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.util.Collections;
import java.util.Map;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AuthToken;
import com.appslandia.plum.base.AuthTokenManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class MemAuthTokenManager implements AuthTokenManager {

  final Map<String, AuthToken> tokenMap = Collections.synchronizedMap(new LruMap<>(100));

  @Override
  public void save(AuthToken authToken) {
    this.tokenMap.put(authToken.getSeries(), copy(authToken));
  }

  @Override
  public AuthToken load(String series) {
    AuthToken authToken = this.tokenMap.get(series);
    if (authToken == null) {
      return null;
    }
    return copy(authToken);
  }

  @Override
  public void reissue(String series, String hashToken, long expiresAt, long issuedAt) {
    AuthToken obj = this.tokenMap.get(series);
    Asserts.notNull(obj);

    obj.setHashToken(hashToken);
    obj.setExpiresAt(expiresAt);
    obj.setIssuedAt(issuedAt);
  }

  @Override
  public void remove(String series) {
    this.tokenMap.remove(series);
  }

  @Override
  public void removeAll(String hashIdentity) {
    this.tokenMap.entrySet().removeIf(e -> e.getValue().getHashIdentity().equals(hashIdentity));
  }

  static AuthToken copy(AuthToken obj) {
    AuthToken copy = new AuthToken();
    copy.setSeries(obj.getSeries());
    copy.setHashToken(obj.getHashToken());
    copy.setHashIdentity(obj.getHashIdentity());

    copy.setExpiresAt(obj.getExpiresAt());
    copy.setIssuedAt(obj.getIssuedAt());
    return copy;
  }
}
