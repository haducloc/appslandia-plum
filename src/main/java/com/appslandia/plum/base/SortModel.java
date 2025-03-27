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
  public static final String REQUEST_ATTRIBUTE_ID = "sortModel";

  final SortConfig config;
  private String sortBy;
  private Boolean sortAsc;

  public SortModel(SortConfig config) {
    this.config = config;
  }

  public SortModel setCurrent(String sortBy, Boolean sortAsc) {
    if (sortBy == null || !this.config.getFields().containsKey(sortBy)) {
      this.sortBy = this.config.getDefBy();
    } else {
      this.sortBy = sortBy;
    }
    this.sortAsc = (sortAsc != null) ? sortAsc : this.config.getFields().get(this.sortBy);
    return this;
  }

  public String getSortBy() {
    return Asserts.notNull(this.sortBy);
  }

  public boolean getSortAsc() {
    return Asserts.notNull(this.sortAsc);
  }

  public Boolean getNext(String fieldName) {
    Arguments.isTrue(this.config.getFields().containsKey(fieldName));
    return this.getSortBy().equals(fieldName) ? !this.getSortAsc() : null;
  }

  public String getState(String fieldName) {
    Arguments.isTrue(this.config.getFields().containsKey(fieldName));

    if (this.getSortBy().equals(fieldName)) {
      return getSortAsc() ? "sort-asc" : "sort-desc";
    }
    return "sort-unsorted";
  }
}
