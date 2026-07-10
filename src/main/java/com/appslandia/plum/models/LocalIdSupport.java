// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Loc Ha
 *
 */
public interface LocalIdSupport {

  Integer getLocalId();

  void setLocalId(Integer localId);

  public static void assignLocalIds(List<? extends LocalIdSupport> subModels) {
    for (var idx = 0; idx < subModels.size(); idx++) {
      LocalIdSupport record = subModels.get(idx);
      record.setLocalId(idx);
    }
  }

  public static void assertLocalIds(List<? extends LocalIdSupport> subModels) {
    Set<Integer> ids = new HashSet<>();
    for (var rec : subModels) {
      var localId = rec.getLocalId();

      if (localId == null) {
        throw new IllegalStateException("The LocalIdSupport.getLocalId() must be not null.");
      }
      if (!ids.add(localId)) {
        throw new IllegalStateException("The LocalIdSupport.getLocalId() must be unique.");
      }
    }
  }
}
