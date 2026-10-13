// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.UUID;

import com.appslandia.plum.base.SessionCsrfManager;

/**
 *
 * @author Loc Ha
 *
 */
public class MockCsrfManager extends SessionCsrfManager {

  public static final String MOCK_CSRF_ID = UUID.randomUUID().toString();

  @Override
  protected String generateCsrfId() {
    return MOCK_CSRF_ID;
  }
}
