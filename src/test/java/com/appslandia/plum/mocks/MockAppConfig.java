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

import com.appslandia.common.base.SimpleConfig;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.defaults.MemModules;

/**
 *
 * @author Loc Ha
 *
 */
public class MockAppConfig extends AppConfig {

  public MockAppConfig() {
    var config = new SimpleConfig();
    config.set(CONFIG_DEFAULT_MODULE, MemModules.MEM_FORM);
    this.config = config;
  }

  public MockAppConfig set(String key, String value) {
    this.config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, boolean value) {
    this.config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, int value) {
    this.config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, long value) {
    this.config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, double value) {
    this.config.set(key, value);
    return this;
  }
}
