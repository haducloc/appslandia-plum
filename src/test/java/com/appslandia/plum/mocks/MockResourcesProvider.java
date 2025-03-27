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

import java.util.Locale;

import com.appslandia.common.base.InitializeException;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.ResourcesProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class MockResourcesProvider extends ResourcesProvider {

  final Locale locale;

  public MockResourcesProvider() {
    this.locale = Locale.getDefault();
  }

  @Override
  protected Resources loadResources(Locale locale) throws InitializeException {
    return new MockResources();
  }

  class MockResources implements Resources {

    @Override
    public String get(Object key) {
      return locale.getLanguage() + ":" + key;
    }

    @Override
    public String get(String key, Object... params) {
      return locale.getLanguage() + ":" + key + "[]";
    }
  }
}
