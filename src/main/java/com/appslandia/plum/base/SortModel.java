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

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;

/**
 *
 * @author Loc Ha
 *
 */
public class SortModel {
  public static final String REQUEST_ATTRIBUTE_ID = "__sort_model";

  final SortConfig config;

  private String __sort_by;
  private Boolean __sort_asc;

  public SortModel(SortConfig config) {
    this.config = config;
  }

  public SortModel setCurrent(String sortBy, Boolean sortAsc) {
    if (sortBy == null || !config.getFields().containsKey(sortBy)) {
      __sort_by = config.getDefby();
    } else {
      __sort_by = sortBy;
    }
    __sort_asc = (sortAsc != null) ? sortAsc : config.getFields().get(__sort_by);
    return this;
  }

  public String get__sort_by() {
    return Asserts.notNull(__sort_by);
  }

  public boolean get__sort_asc() {
    return Asserts.notNull(__sort_asc);
  }

  public Boolean nextAsc(String fieldName) {
    Arguments.isTrue(config.getFields().containsKey(fieldName));
    return get__sort_by().equals(fieldName) ? !get__sort_asc() : null;
  }

  public String curCss(String fieldName) {
    Arguments.isTrue(config.getFields().containsKey(fieldName));

    if (get__sort_by().equals(fieldName)) {
      return get__sort_asc() ? "l-sort-asc" : "l-sort-desc";
    }
    return "l-sort-unsorted";
  }
}
