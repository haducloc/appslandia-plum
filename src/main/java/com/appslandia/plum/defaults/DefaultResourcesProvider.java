// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

package com.appslandia.plum.defaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

import com.appslandia.common.base.InitializeException;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.ResourcesProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultResourcesProvider extends ResourcesProvider {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ServletContext servletContext;

  @Override
  protected Resources loadResources(Locale locale) throws InitializeException {
    Properties props = new Properties();
    String[] resourceNames = this.appConfig.getStringArray(AppConfig.CONFIG_RESOURCE_NAMES);

    for (String resourceName : resourceNames) {
      String resPath = getResourcePath(resourceName, locale);

      InputStream is = this.servletContext.getResourceAsStream(resPath);
      if (is != null) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
          loadResources(props, br);

        } catch (IOException ex) {
          throw new InitializeException(ex);
        }
      }
    }
    return newResources(props);
  }

  protected String getResourcePath(String resourceName, Locale locale) {
    return STR.fmt("/WEB-INF/resources/{}.{}.properties", resourceName, locale.getLanguage());
  }

  protected void loadResources(Properties resources, BufferedReader br) throws IOException {
    resources.load(br);
  }
}
