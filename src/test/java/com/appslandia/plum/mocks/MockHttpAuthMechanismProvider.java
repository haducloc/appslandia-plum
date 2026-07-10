// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.List;
import java.util.stream.Collectors;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.HttpAuthMechanismBase;
import com.appslandia.plum.base.HttpAuthMechanismProvider;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpAuthMechanismProvider extends HttpAuthMechanismProvider {

  @Inject
  protected Instance<HttpAuthenticationMechanism> instance;

  @Override
  protected void init() throws Exception {
    List<HttpAuthenticationMechanism> impls = instance.stream().collect(Collectors.toList());

    for (HttpAuthenticationMechanism impl : impls) {
      if (impl instanceof HttpAuthMechanismBase authMechanism) {

        var mappedID = authMechanism.getClass().getDeclaredAnnotation(MappedID.class);
        Asserts.notNull(mappedID);

        registerMechanism(mappedID.value(), authMechanism);
      }
    }
    super.init();
  }
}
