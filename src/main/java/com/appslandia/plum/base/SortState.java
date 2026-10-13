// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class SortState {
  final String sortBy;
  final boolean sortAsc;

  public SortState(String sortBy, boolean sortAsc) {
    this.sortBy = Arguments.notNull(sortBy);
    this.sortAsc = sortAsc;
  }

  public boolean isForField(String fieldName) {
    return sortBy.equalsIgnoreCase(fieldName);
  }

  public String getSortBy() {
    return sortBy;
  }

  public boolean isSortAsc() {
    return sortAsc;
  }
}
