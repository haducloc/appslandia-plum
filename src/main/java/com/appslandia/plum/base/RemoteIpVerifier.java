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

import java.util.regex.Pattern;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Patterns;

/**
 *
 * @author Loc Ha
 *
 */
public class RemoteIpVerifier extends InitializeObject {

  private Pattern[] denies;
  private Pattern[] allows;

  @Override
  protected void init() throws Exception {
    if (this.denies == null) {
      this.denies = Patterns.EMPTY_PATTERNS;
    }
    if (this.allows == null) {
      this.allows = Patterns.EMPTY_PATTERNS;
    }
  }

  public RemoteIpVerifier setDenies(String... denyPatterns) {
    this.assertNotInitialized();
    this.denies = Patterns.compile(denyPatterns);
    return this;
  }

  public RemoteIpVerifier setAllows(String... allowPatterns) {
    this.assertNotInitialized();
    this.allows = Patterns.compile(allowPatterns);
    return this;
  }

  public boolean allow(String remoteIp) {
    this.initialize();

    // Check the deny patterns, if any
    if (Patterns.matches(this.denies, remoteIp)) {
      return false;
    }

    // Check the allow patterns, if any
    if (Patterns.matches(this.allows, remoteIp)) {
      return true;
    }

    // If allows configured -> false
    if (this.allows.length != 0) {
      return false;
    }
    return true;
  }
}
