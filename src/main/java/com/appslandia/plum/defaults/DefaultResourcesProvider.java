// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.util.Properties;

import com.appslandia.common.base.Language;
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
  protected Resources createResources(Properties res, Language language) {
    return new ResourcesImpl(res, language);
  }
}
