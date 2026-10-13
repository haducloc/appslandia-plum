// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AppLogger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class OAuth2ConfigProvider {

  protected Map<String, OAuth2Config> configMap = new HashMap<>();

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected BeanManager beanManager;

  @PostConstruct
  protected void initialize() {
    CDIUtils.<OAuth2Config>consumeReferences(beanManager, OAuth2Config.class, new Annotation[] { Any.Literal.INSTANCE },
        (bc, br) -> {
          var inst = br.get();
          Asserts.isTrue(br.isApplicationScoped() || br.isSingleton());

          appLogger.info("Installing OAuth2Config {}: {}", inst.getProvider(), inst);
          configMap.put(inst.getProvider(), inst);
        });

    configMap = Collections.unmodifiableMap(configMap);
  }

  public OAuth2Config getConfig(String provider) {
    Arguments.notNull(provider);

    var impl = configMap.get(provider);
    return Arguments.notNull(impl, "No OAuth2Config is registered for provider '{}'.", provider);
  }
}
