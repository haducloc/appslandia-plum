// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.cdi.CDIEventListener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AuthEventListener implements CDIEventListener<AuthEvent> {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AuthEventManager authEventManager;

  @Override
  public void onEvent(@Observes AuthEvent event) {
    try {
      authEventManager.save(event);

    } catch (Exception ex) {
      appLogger.error(ex);
    }
  }

  @Override
  public void onEventAsync(@ObservesAsync AuthEvent event) {
    throw new UnsupportedOperationException();
  }
}
