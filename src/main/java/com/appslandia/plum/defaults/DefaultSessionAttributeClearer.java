// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.STR;
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

  public static final String CONFIG_SESSION_APP_ATTRIBUTES = DefaultSessionAttributeClearer.class.getName()
      + ".session_app_attributes";

  private static final Set<String> KNOWN_APP_ATTRIBUTES = CollectionUtils.toUnmodifiableSet(
      SessionCsrfManager.SESSION_ATTRIBUTE_CSRF_STORE, SessionTempDataManager.SESSION_ATTRIBUTE_TEMP_DATA_STORE);

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private Set<String> attributesToRemove;

  @PostConstruct
  protected void initialize() {
    var attrs = appConfig.getStringArray(CONFIG_SESSION_APP_ATTRIBUTES);
    attributesToRemove = CollectionUtils.toUnmodifiableSet(attrs);

    appLogger.info(STR.fmt("{}: {}", CONFIG_SESSION_APP_ATTRIBUTES, attributesToRemove));
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
    if (KNOWN_APP_ATTRIBUTES.contains(attributeName)) {
      return true;
    }
    return attributesToRemove.contains(attributeName);
  }
}
