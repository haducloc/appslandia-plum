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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class ResourcesImpl implements Resources {

  final Properties res;
  final Locale locale;

  public ResourcesImpl(Properties res, Locale locale) {
    this.res = res;
    this.locale = locale;
  }

  public Locale getLocale() {
    return this.locale;
  }

  @Override
  public String get(Object key) {
    Arguments.notNull(key);
    var value = (String) this.res.get(key);
    if (value == null) {
      return this.locale.getLanguage() + ":" + key;
    }
    return value;
  }

  @Override
  public String get(String key, Object... params) {
    Arguments.notNull(key);

    var value = (String) this.res.get(key);
    if (value == null) {
      return this.locale.getLanguage() + ":" + key;
    }
    var mf = new MessageFormat(value, this.locale);
    return mf.format(params);
  }
}
