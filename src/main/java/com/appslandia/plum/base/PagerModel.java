// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.ArrayList;
import java.util.Collections;
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

  public static final String PARAM_PAGE_INDEX = "__page_index";
  public static final String PARAM_RECORD_COUNT = "__record_count";

  final int recordCount;
  final int pageCount;
  final PagerState state;

  final List<PagerItem> items;

  public PagerModel(int recordCount, PagerState state) {
    this(recordCount, state, PagerSize.SIZE_9);
  }

  public PagerModel(int recordCount, PagerState state, PagerSize pagerSize) {
    Arguments.isTrue(recordCount >= 0);
    Arguments.notNull(state);
    Arguments.notNull(pagerSize);

    this.state = state;
    this.recordCount = recordCount;

    var pageIndex = state.pageIndex;
    pageCount = (recordCount > 0) ? (int) Math.ceil(recordCount / (double) state.pageSize) : 1;

    // Why 6? PREV + 1L + dotL & dotR + 1R + NEXT
    var beginIndex = ValueUtils.valueOrMin(pageIndex - ((pagerSize.size - 6) / 2), 1);
    var endIndex = ValueUtils.valueOrMax(pageIndex + ((pagerSize.size - 6) / 2), pageCount);

    // PREV
    List<PagerItem> items = new ArrayList<>();
    if (pageIndex > 1) {
      items.add(new PagerItem(PagerItem.ItemType.PREV, pageIndex - 1));
    }
    boolean dotL = false, dotR = false;

    // 1 ...
    if (beginIndex == 2) {
      items.add(new PagerItem(PagerItem.ItemType.INDEX, 1));
    } else if (beginIndex > 2) {
      items.add(new PagerItem(PagerItem.ItemType.INDEX, 1));
      items.add(new PagerItem(PagerItem.ItemType.DOT, Integer.MIN_VALUE));
      dotL = true;
    }

    // Indexes
    for (var index = beginIndex; index <= endIndex; index++) {
      items.add(new PagerItem(PagerItem.ItemType.INDEX, index));
    }

    // ... N
    if (endIndex == pageCount - 1) {
      items.add(new PagerItem(PagerItem.ItemType.INDEX, pageCount));
    } else if (endIndex < pageCount - 1) {
      items.add(new PagerItem(PagerItem.ItemType.DOT, Integer.MAX_VALUE));
      items.add(new PagerItem(PagerItem.ItemType.INDEX, pageCount));
      dotR = true;
    }

    // NEXT
    if (pageIndex < pageCount) {
      items.add(new PagerItem(PagerItem.ItemType.NEXT, pageIndex + 1));
    }

    // Add more items
    if (dotR) {
      var addIndex = endIndex + 1;
      while ((items.size() < pagerSize.size) && (addIndex + 1 < pageCount)) {
        var idx = items.indexOf(new PagerItem(PagerItem.ItemType.INDEX, addIndex - 1));
        items.add(idx + 1, new PagerItem(PagerItem.ItemType.INDEX, addIndex));
        addIndex++;
      }
    }
    if (dotL) {
      var addIndex = beginIndex - 1;
      while ((items.size() < pagerSize.size) && (addIndex - 1 > 1)) {
        var idx = items.indexOf(new PagerItem(PagerItem.ItemType.INDEX, addIndex + 1));
        items.add(idx, new PagerItem(PagerItem.ItemType.INDEX, addIndex));
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
          items.set(idx, new PagerItem(PagerItem.ItemType.INDEX, before.getIndex() + 1));
        }
      }
    }
    this.items = Collections.unmodifiableList(items);
  }

  public int getRecordCount() {
    return recordCount;
  }

  public int getPageCount() {
    return pageCount;
  }

  public PagerState getState() {
    return state;
  }

  public List<PagerItem> getItems() {
    return items;
  }

  public enum PagerSize {
    SIZE_9(9), SIZE_11(11);

    final int size;

    private PagerSize(int size) {
      this.size = size;
    }

    public int size() {
      return size;
    }
  }
}
