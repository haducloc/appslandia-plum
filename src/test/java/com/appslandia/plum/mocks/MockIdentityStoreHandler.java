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

package com.appslandia.plum.mocks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.appslandia.plum.base.IdentityStoreBase;
import com.appslandia.plum.base.IdentityStoreHandlerBase;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
public class MockIdentityStoreHandler extends IdentityStoreHandlerBase {

  @Inject
  protected Instance<IdentityStore> instance;

  @Override
  protected void init() throws Exception {
    List<IdentityStoreBase> impls = this.instance.stream().filter(s -> s instanceof IdentityStoreBase)
        .map(s -> (IdentityStoreBase) s).collect(Collectors.toList());

    Collections.sort(impls, (s1, s2) -> {
      return Integer.valueOf(s1.priority()).compareTo(s2.priority());
    });

    this.identityStores.addAll(impls);
    super.init();
  }
}
