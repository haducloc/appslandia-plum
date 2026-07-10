// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class PagerState {
  final int pageIndex;
  final int pageSize;

  public PagerState(Integer pageIndex, int pageSize) {
    Arguments.isTrue(pageSize > 0);

    this.pageIndex = ValueUtils.valueOrMin(pageIndex, 1);
    this.pageSize = pageSize;
  }

  public int getSkipCount() {
    return (pageIndex - 1) * pageSize;
  }

  public int getPageIndex() {
    return pageIndex;
  }

  public int getPageSize() {
    return pageSize;
  }
}
