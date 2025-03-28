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

package com.appslandia.plum.base;

import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.BaseEncoder;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class BasicAuthHandler extends HttpAuthHandler {

  @Override
  public AuthMethod getAuthMethod() {
    return AuthMethod.BASIC;
  }

  protected abstract UsernamePasswordCredential createCredential(String username, String password);

  @Override
  protected Credential doParseCredential(String credential) throws Exception {
    String usernamePwd = new String(BaseEncoder.BASE64.decode(credential), StandardCharsets.UTF_8);

    // username:password
    int idx = usernamePwd.indexOf(":");
    if (idx < 0) {
      return null;
    }
    String username = usernamePwd.substring(0, idx).strip();
    String password = usernamePwd.substring(idx + 1);

    if (username.isEmpty() || password.isEmpty()) {
      return null;
    }
    return createCredential(username, password);
  }
}
