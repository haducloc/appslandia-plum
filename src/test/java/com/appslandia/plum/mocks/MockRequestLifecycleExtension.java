// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 *
 * @author Loc Ha
 *
 */
public class MockRequestLifecycleExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    MockContainer.currentRequestHolder.set(MockContainer.containerHolder.get().createRequest());
    MockContainer.currentResponseHolder.set(MockContainer.containerHolder.get().createResponse());
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    MockContainer.currentRequestHolder.set(null);
    MockContainer.currentResponseHolder.set(null);
  }
}
