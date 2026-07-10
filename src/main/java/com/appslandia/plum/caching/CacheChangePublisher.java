// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.caching;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class CacheChangePublisher {

  @Inject
  protected Event<CacheChange> event;

  public void fire(String cacheName, String... keys) {
    event.fire(new CacheChange(cacheName, keys));
  }
}
