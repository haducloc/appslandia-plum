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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthorizePolicyProvider extends InitializeObject {

  private Map<String, AuthorizePolicy> authorizePolicyMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    this.authorizePolicyMap = Collections.unmodifiableMap(this.authorizePolicyMap);
  }

  public AuthorizePolicy getAuthorizePolicy(String name) {
    this.initialize();
    AuthorizePolicy impl = this.authorizePolicyMap.get(name);
    return Arguments.notNull(impl);
  }

  public void addAuthorizePolicy(String name, AuthorizePolicy impl) {
    this.assertNotInitialized();
    this.authorizePolicyMap.put(name, impl);
  }

  public boolean authorize(UserPrincipal principal, String... policies) {
    this.initialize();
    Arguments.notNull(principal);
    Arguments.hasElements(policies);

    for (String policy : policies) {
      if (getAuthorizePolicy(policy).authorize(principal)) {
        return true;
      }
    }
    return false;
  }
}
