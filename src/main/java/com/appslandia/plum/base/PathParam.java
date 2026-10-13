// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.List;

import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class PathParam {

  private String paramName;
  private List<PathParam> subParams;

  public PathParam(String paramName) {
    this.paramName = paramName;
  }

  public PathParam(List<PathParam> subParams) {
    this.subParams = CollectionUtils.toUnmodifiableList(subParams);
  }

  public boolean hasPathParam(String paramName) {
    if (this.paramName != null) {
      if (this.paramName.equalsIgnoreCase(paramName)) {
        return true;
      }
    } else if (subParams.stream().anyMatch(p -> p.getParamName().equalsIgnoreCase(paramName))) {
      return true;
    }
    return false;
  }

  public String getParamName() {
    return paramName;
  }

  public List<PathParam> getSubParams() {
    return subParams;
  }
}
