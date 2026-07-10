// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.UUID;

import com.appslandia.plum.base.SessionTempDataManager;

/**
 *
 * @author Loc Ha
 *
 */
public class MockTempDataManager extends SessionTempDataManager {

  public static final String MOCK_TEMP_DATA_ID = UUID.randomUUID().toString();

  @Override
  protected String generateTempDataId() {
    return MOCK_TEMP_DATA_ID;
  }
}
