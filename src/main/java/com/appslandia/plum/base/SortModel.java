// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class SortModel extends InitializingObject {
  public static final String REQUEST_ATTRIBUTE_ID = "__sort_model";

  public static final String PARAM_SORT_BY = "__sort_by";
  public static final String PARAM_SORT_ASC = "__sort_asc";

  final SortConfig config;
  private SortState state;

  public SortModel(SortConfig config) {
    this.config = Arguments.notNull(config);
  }

  @Override
  protected void init() throws Exception {
    Arguments.notNull(state, "state is required.");
  }

  public SortModel setState(String sortBy, Boolean sortAsc) {
    assertNotInitialized();

    var optSortBy = config.getDefBy();
    if (sortBy != null && config.hasField(sortBy)) {
      optSortBy = sortBy;
    }
    boolean optSortAsc = (sortAsc != null) ? sortAsc : config.getSortAsc(optSortBy);

    state = new SortState(optSortBy, optSortAsc);
    return this;
  }

  public SortConfig getConfig() {
    initialize();
    return config;
  }

  public SortState getState() {
    initialize();
    return state;
  }

  public boolean isCurrentSort(String fieldName) {
    initialize();
    Arguments.isTrue(config.hasField(fieldName));

    return state.isForField(fieldName);
  }

  public Boolean nextSortAsc(String fieldName) {
    return isCurrentSort(fieldName) ? !state.sortAsc : null;
  }

  public String getSortClass(String fieldName) {
    if (isCurrentSort(fieldName)) {
      return state.sortAsc ? "l-sortable l-sort-asc" : "l-sortable l-sort-desc";
    }
    return "l-sortable l-sort-none";
  }
}
