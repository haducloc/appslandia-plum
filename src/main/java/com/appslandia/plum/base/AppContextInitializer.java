// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.beans.Introspector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.base.CleanupManager;
import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.utils.ObjectUtils;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.interceptor.Interceptor;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AppContextInitializer {

  public static final String CONTEXT_ATTRIBUTE_ENV = "__env";
  public static final String CONTEXT_ATTRIBUTE_MUTEX = "__mutex";
  public static final String CONTEXT_ATTRIBUTE_BEAN_INSTANCES = "__bean_instances";

  // AppContextInitializer can be invoked manually,
  // so define a state attribute to track its execution status.

  // new AppContextInitializer().contextInitialized(sc);
  // new AppContextInitializer().contextDestroyed(sc);

  public static final String CONTEXT_APP_CONTEXT_INITIALIZER_STATE = AppContextInitializer.class.getName() + ".state";

  public void contextInitialized(
      @Observes @Initialized(ApplicationScoped.class) @Priority(Interceptor.Priority.LIBRARY_BEFORE
          - 500) ServletContext sc) {

    if (sc.getAttribute(CONTEXT_APP_CONTEXT_INITIALIZER_STATE) != InitializerState.INITIALIZED) {
      sc.setAttribute(CONTEXT_APP_CONTEXT_INITIALIZER_STATE, InitializerState.INITIALIZED);

      sc.setAttribute(CONTEXT_ATTRIBUTE_ENV, DeployEnv.getCurrent());
      sc.setAttribute(CONTEXT_ATTRIBUTE_MUTEX, new Object());

      sc.log("Registering bean instances holder in the servlet context...");
      getBeanInstances(sc);
    }
  }

  public void contextDestroyed(
      @Observes @Destroyed(ApplicationScoped.class) @Priority(Interceptor.Priority.LIBRARY_AFTER
          + 500) ServletContext sc) {

    if (sc.getAttribute(CONTEXT_APP_CONTEXT_INITIALIZER_STATE) != InitializerState.DESTROYED) {
      sc.setAttribute(CONTEXT_APP_CONTEXT_INITIALIZER_STATE, InitializerState.DESTROYED);

      sc.log("Destroying bean instances stored in the servlet context...");
      destroyBeanInstances(sc);

      sc.log("Running CleanupManager.cleanup()...");
      CleanupManager.cleanup();

      sc.removeAttribute(CONTEXT_ATTRIBUTE_MUTEX);
      sc.removeAttribute(CONTEXT_ATTRIBUTE_ENV);

      sc.log("Running Introspector.flushCaches()...");
      Introspector.flushCaches();
    }
  }

  private static final Object MUTEX = new Object();

  public static Map<InstanceKey, BeanInstance<?>> getBeanInstances(ServletContext sc) {
    Map<InstanceKey, BeanInstance<?>> beanInsts = ObjectUtils.cast(sc.getAttribute(CONTEXT_ATTRIBUTE_BEAN_INSTANCES));
    if (beanInsts == null) {

      synchronized (MUTEX) {
        beanInsts = ObjectUtils.cast(sc.getAttribute(CONTEXT_ATTRIBUTE_BEAN_INSTANCES));

        if (beanInsts == null) {
          beanInsts = new ConcurrentHashMap<>();

          sc.setAttribute(CONTEXT_ATTRIBUTE_BEAN_INSTANCES, beanInsts);
        }
      }
    }
    return beanInsts;
  }

  public static void destroyBeanInstances(ServletContext sc) {
    Map<InstanceKey, BeanInstance<?>> beanInsts = ObjectUtils.cast(sc.getAttribute(CONTEXT_ATTRIBUTE_BEAN_INSTANCES));
    if (beanInsts != null) {

      beanInsts.values().stream().forEach(bi -> {
        try {
          bi.destroy();

        } catch (RuntimeException ex) {
          sc.log(ex.getMessage(), ex);
        }
      });
      sc.removeAttribute(CONTEXT_ATTRIBUTE_BEAN_INSTANCES);
    }
  }

  private enum InitializerState {
    INITIALIZED, DESTROYED
  }
}
