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

import java.util.ArrayList;
import java.util.List;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class PagerModel {

  public static final String REQUEST_ATTRIBUTE_ID = "__pager_model";

  final int pageIndex;
  final int recordCount;

  final int pageCount;
  final List<PagerItem> items;

  public PagerModel(int pageIndex, int recordCount, int pageSize) {
    this(pageIndex, recordCount, pageSize, PagerSize.SIZE_9);
  }

  public PagerModel(int pageIndex, int recordCount, int pageSize, PagerSize pagerSize) {
    Arguments.isTrue(pageIndex >= 1);
    Arguments.isTrue(recordCount >= 0);
    Arguments.isTrue(pageSize > 0);
    Arguments.notNull(pagerSize);

    this.pageIndex = pageIndex;
    this.recordCount = recordCount;
    this.pageCount = (recordCount > 0) ? (int) Math.ceil(recordCount / (double) pageSize) : 1;

    // Why 6? PREV + 1L + dotL & dotR + 1R + NEXT
    var beginIndex = ValueUtils.valueOrMin(pageIndex - ((pagerSize.size - 6) / 2), 1);
    var endIndex = ValueUtils.valueOrMax(pageIndex + ((pagerSize.size - 6) / 2), this.pageCount);

    // PREV
    List<PagerItem> items = new ArrayList<>();
    if (pageIndex > 1) {
      items.add(new PagerItem(PagerItem.TYPE_PREV, pageIndex - 1));
    }
    boolean dotL = false, dotR = false;

    // 1 ...
    if (beginIndex == 2) {
      items.add(new PagerItem(PagerItem.TYPE_INDEX, 1));
    } else if (beginIndex > 2) {
      items.add(new PagerItem(PagerItem.TYPE_INDEX, 1));
      items.add(new PagerItem(PagerItem.TYPE_DOT, -1));
      dotL = true;
    }

    // Indexes
    for (var index = beginIndex; index <= endIndex; index++) {
      items.add(new PagerItem(PagerItem.TYPE_INDEX, index));
    }

    // ... N
    if (endIndex == this.pageCount - 1) {
      items.add(new PagerItem(PagerItem.TYPE_INDEX, this.pageCount));
    } else if (endIndex < this.pageCount - 1) {
      items.add(new PagerItem(PagerItem.TYPE_DOT, -1));
      items.add(new PagerItem(PagerItem.TYPE_INDEX, this.pageCount));
      dotR = true;
    }

    // NEXT
    if (pageIndex < this.pageCount) {
      items.add(new PagerItem(PagerItem.TYPE_NEXT, pageIndex + 1));
    }

    // Add more items
    if (dotR) {
      var addIndex = endIndex + 1;
      while ((items.size() < pagerSize.size) && (addIndex + 1 < this.pageCount)) {
        var idx = items.indexOf(new PagerItem(PagerItem.TYPE_INDEX, addIndex - 1));
        items.add(idx + 1, new PagerItem(PagerItem.TYPE_INDEX, addIndex));
        addIndex++;
      }
    }
    if (dotL) {
      var addIndex = beginIndex - 1;
      while ((items.size() < pagerSize.size) && (addIndex - 1 > 1)) {
        var idx = items.indexOf(new PagerItem(PagerItem.TYPE_INDEX, addIndex + 1));
        items.add(idx, new PagerItem(PagerItem.TYPE_INDEX, addIndex));
        addIndex--;
      }
    }

    // Replace dots
    for (var idx = 0; idx < items.size(); idx++) {
      var curItem = items.get(idx);
      if (curItem.isDotType()) {

        var before = items.get(idx - 1);
        var after = items.get(idx + 1);

        if (before.getIndex() + 2 == after.getIndex()) {
          items.set(idx, new PagerItem(PagerItem.TYPE_INDEX, before.getIndex() + 1));
        }
      }
    }
    this.items = items;
  }

  public int getPageIndex() {
    return this.pageIndex;
  }

  public int getRecordCount() {
    return this.recordCount;
  }

  public int getPageCount() {
    return this.pageCount;
  }

  public List<PagerItem> getItems() {
    return this.items;
  }

  public enum PagerSize {
    SIZE_9(9), SIZE_11(11);

    final int size;

    private PagerSize(int size) {
      this.size = size;
    }

    public int size() {
      return this.size;
    }
  }
}
