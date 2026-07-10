// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.AccessLog;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.JulAppLogger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAppLoggerFactory {

  @Produces
  public AppLogger produce(InjectionPoint ip) {
    return new JulAppLogger(ip.getMember().getDeclaringClass());
  }

  public void dispose(@Disposes AppLogger impl) {
  }

  @Produces
  @AccessLog
  public AppLogger produceAccessLog(InjectionPoint ip) {
    return new JulAppLogger(AccessLog.class);
  }

  public void disposeAccessLog(@Disposes @AccessLog AppLogger impl) {
  }
}
