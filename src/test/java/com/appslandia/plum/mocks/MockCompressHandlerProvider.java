// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.List;
import java.util.stream.Collectors;

import com.appslandia.plum.base.CompressHandler;
import com.appslandia.plum.base.CompressHandlerProvider;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public class MockCompressHandlerProvider extends CompressHandlerProvider {

  @Inject
  protected Instance<CompressHandler> instance;

  @Override
  protected void init() throws Exception {
    List<CompressHandler> impls = instance.stream().collect(Collectors.toList());

    for (CompressHandler impl : impls) {
      registerHandler(impl);
    }
    super.init();
  }
}
