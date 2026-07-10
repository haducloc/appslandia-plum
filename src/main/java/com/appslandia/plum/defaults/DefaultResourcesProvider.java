// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Locale;
import java.util.Properties;

import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.ResourcesImpl;
import com.appslandia.plum.base.ServletResourcesProvider;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultResourcesProvider extends ServletResourcesProvider {

  @Override
  protected Resources createResource(Properties res, Locale locale) {
    return new ResourcesImpl(res, locale);
  }
}
