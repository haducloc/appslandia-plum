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

package com.appslandia.plum.base;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ResourcesImpl implements Resources {

  final ResourceBundle bundle;

  public ResourcesImpl(ResourceBundle bundle) {
    this.bundle = bundle;
  }

  public Locale getLocale() {
    return this.bundle.getLocale();
  }

  @Override
  public String get(Object key) {
    Arguments.notNull(key);

    return this.bundle.getString((String) key);
  }

  @Override
  public String get(String key, Object... params) {
    Arguments.notNull(key);

    String format = this.bundle.getString(key);
    MessageFormat mf = new MessageFormat(format, this.bundle.getLocale());
    return mf.format(params);
  }
}
