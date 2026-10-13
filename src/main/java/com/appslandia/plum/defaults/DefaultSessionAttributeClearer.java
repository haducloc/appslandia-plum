// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Set;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.AppSessionInitializer;
import com.appslandia.plum.base.SessionAttributeClearer;
import com.appslandia.plum.base.SessionCsrfManager;
import com.appslandia.plum.base.SessionTempDataManager;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultSessionAttributeClearer implements SessionAttributeClearer {

  public static final String CONFIG_SESSION_REMOVE_ATTRIBUTES = "config.session_remove_attributes_on_auth";

  private static final Set<String> KNOWN_REMOVE_ATTRIBUTES = Set.of(SessionCsrfManager.SESSION_ATTRIBUTE_CSRF_STORE,
      SessionTempDataManager.SESSION_ATTRIBUTE_TEMP_DATA_STORE);

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private Set<String> attributesToRemove;

  @PostConstruct
  protected void initialize() {
    var attrs = appConfig.getStringArray(CONFIG_SESSION_REMOVE_ATTRIBUTES);
    attributesToRemove = Set.of(attrs);

    appLogger.info("{}: {}", CONFIG_SESSION_REMOVE_ATTRIBUTES, attributesToRemove);
  }

  @Override
  public void clearSession(HttpSession session) {
    var mutex = ServletUtils.getMutex(session);

    synchronized (mutex) {
      var names = session.getAttributeNames();

      while (names.hasMoreElements()) {
        var attr = names.nextElement();

        if (shouldRemove(attr)) {
          session.removeAttribute(attr);
        }
      }
    }
  }

  protected boolean shouldRemove(String attributeName) {
    if (AppSessionInitializer.SESSION_ATTRIBUTE_MUTEX.equals(attributeName)) {
      return false;
    }
    if (KNOWN_REMOVE_ATTRIBUTES.contains(attributeName)) {
      return true;
    }
    return attributesToRemove.contains(attributeName);
  }
}
