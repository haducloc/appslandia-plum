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

import java.util.Locale;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionRoute {

  final String controller;
  final String action;

  public ActionRoute(String controller, String action) {
    this.controller = controller;
    this.action = action;
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + this.controller.toLowerCase(Locale.ENGLISH).hashCode();
    hash = p * hash + this.action.toLowerCase(Locale.ENGLISH).hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ActionRoute that)) {
      return false;
    }
    return this.controller.equalsIgnoreCase(that.controller) && this.action.equalsIgnoreCase(that.action);
  }

  @Override
  public String toString() {
    return '/' + this.controller + '/' + this.action;
  }
}
