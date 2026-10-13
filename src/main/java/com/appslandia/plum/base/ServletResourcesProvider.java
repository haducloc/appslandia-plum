// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.Language;
import com.appslandia.common.utils.Asserts;
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
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ServletContext servletContext;

  @Override
  protected Resources loadResources(String module, Language language) throws InitializingException {
    // {module}.resource_names";
    var resourceNames = appConfig.getStringArray(module + ".resource_names");

    appLogger.info("Loading resource files: {} for module: {}", Arrays.toString(resourceNames), module);
    var res = new Properties();

    for (String resourceName : resourceNames) {
      // resourceName: {}.messages or common
      resourceName = STR.fmt(resourceName, module);

      var resPath = STR.fmt("/WEB-INF/resources/{}.{}.properties", resourceName, language.getId());
      appLogger.info("Loading resource file: {}", resPath);

      var resStream = servletContext.getResourceAsStream(resPath);
      Asserts.notNull(resStream, "Resource file '{}' was not found.", resPath);

      try (var br = new BufferedReader(new InputStreamReader(resStream, StandardCharsets.UTF_8))) {
        loadResources(res, br);

      } catch (IOException ex) {
        throw new InitializingException(ex);
      }
    }
    return createResources(res, language);
  }

  protected abstract Resources createResources(Properties res, Language language);

  protected void loadResources(Properties res, BufferedReader br) throws IOException {
    res.load(br);
  }
}
