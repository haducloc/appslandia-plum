// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

import com.appslandia.common.base.InitializingException;
import com.appslandia.common.utils.STR;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ServletResourcesProvider extends ResourcesProvider {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ServletContext servletContext;

  @Override
  protected Resources loadResources(Locale locale) throws InitializingException {
    var res = new Properties();
    var resourceNames = appConfig.getStringArray(AppConfig.CONFIG_RESOURCE_NAMES);

    for (String resourceName : resourceNames) {
      var resPath = getResourcePath(resourceName, locale);

      var is = servletContext.getResourceAsStream(resPath);
      if (is != null) {
        try (var br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
          loadResources(res, br);

        } catch (IOException ex) {
          throw new InitializingException(ex);
        }
      }
    }
    return createResource(res, locale);
  }

  protected abstract Resources createResource(Properties res, Locale locale);

  protected String getResourcePath(String resourceName, Locale locale) {
    return STR.fmt("/WEB-INF/resources/{}.{}.properties", resourceName, locale.getLanguage());
  }

  protected void loadResources(Properties res, BufferedReader br) throws IOException {
    res.load(br);
  }
}
