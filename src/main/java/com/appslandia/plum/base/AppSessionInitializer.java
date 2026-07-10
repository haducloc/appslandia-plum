// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.Mutex;
import com.appslandia.common.utils.Asserts;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AppSessionInitializer {

  public static final String SESSION_ATTRIBUTE_MUTEX = "__mutex";

  @Inject
  protected AppConfig appConfig;

  public void sessionInitialized(@Observes @Initialized(SessionScoped.class) HttpSession session) {
    Asserts.isTrue(appConfig.isEnableSession(), "appConfig.isEnableSession() is false.");

    session.setAttribute(SESSION_ATTRIBUTE_MUTEX, new Mutex());
  }

  public void sessionDestroyed(@Observes @Destroyed(SessionScoped.class) HttpSession session) {
    try {
      session.removeAttribute(SESSION_ATTRIBUTE_MUTEX);
    } catch (IllegalStateException ex) {
      // ignore
    }
  }
}
