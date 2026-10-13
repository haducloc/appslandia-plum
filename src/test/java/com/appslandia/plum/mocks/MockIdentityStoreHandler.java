// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.List;
import java.util.stream.Collectors;

import com.appslandia.plum.base.IdentityStoreBase;
import com.appslandia.plum.base.IdentityStoreHandlerBase;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
public class MockIdentityStoreHandler extends IdentityStoreHandlerBase {

  @Inject
  protected Instance<IdentityStore> instance;

  @Override
  protected void init() throws Exception {
    List<IdentityStoreBase> impls = instance.stream().filter(s -> s instanceof IdentityStoreBase)
        .map(s -> (IdentityStoreBase) s).collect(Collectors.toList());

    identityStores.addAll(impls);
    super.init();
  }
}
