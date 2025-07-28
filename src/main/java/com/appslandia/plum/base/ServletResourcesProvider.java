// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

import com.appslandia.common.base.InitializeException;
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
  protected Resources loadResources(Locale locale) throws InitializeException {
    var res = new Properties();
    var resourceNames = this.appConfig.getStringArray(AppConfig.CONFIG_RESOURCE_NAMES);

    for (String resourceName : resourceNames) {
      var resPath = getResourcePath(resourceName, locale);

      var is = this.servletContext.getResourceAsStream(resPath);
      if (is != null) {
        try (var br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
          loadResources(res, br);

        } catch (IOException ex) {
          throw new InitializeException(ex);
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
