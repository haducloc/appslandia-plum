// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class DefaultPebbleTemplateProvider extends PebbleTemplateProvider {

  @Inject
  protected ServletContext servletContext;

  @Override
  protected Loader<?> getLoader() {
    return new Servlet5Loader(servletContext);
  }
}
