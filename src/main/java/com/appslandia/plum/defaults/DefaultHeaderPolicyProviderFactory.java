// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.plum.base.HeaderPolicyProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultHeaderPolicyProviderFactory implements CDIFactory<HeaderPolicyProvider> {

  @Produces
  @ApplicationScoped
  @Override
  public HeaderPolicyProvider produce() {
    return new HeaderPolicyProvider();
  }

  @Override
  public void dispose(@Disposes HeaderPolicyProvider impl) {
  }
}
