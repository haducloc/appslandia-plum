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

package com.appslandia.plum.base;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.appslandia.common.utils.STR;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class HandlerMappingsRegister implements Startup {

  protected String[] getLanguageIds() {
    return null;
  }

  protected DynMultipartConfig buildMultipartConfig() {
    return new DynMultipartConfig();
  }

  @Override
  public void onStartup(ServletContext sc, List<Class<? extends Startup>> startupClasses) throws ServletException {
    ActionScanner scanner = ActionScanner.getInstance();

    Set<String> urlMappings = new TreeSet<>();
    urlMappings.add("");

    String[] languages = getLanguageIds();

    for (Class<?> controllerClass : scanner.getControllerClasses()) {
      String controller = ActionDescProvider.getController(controllerClass);

      // /{controller}/*
      urlMappings.add(STR.fmt("/{}/*", controller));

      // Only register language mappings if languages > 1
      if ((languages != null) && (languages.length > 1)) {
        for (String language : languages) {

          // /{language}/{controller}/*
          urlMappings.add(STR.fmt("/{}/{}/*", language, controller));
        }
      }
    }

    boolean partsSupported = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableParts.class) != null);
    DynMultipartConfig multipartConfig = partsSupported ? buildMultipartConfig() : null;

    String executorName = ExecutorHandler.class.getSimpleName();
    String initializerName = InitializerHandler.class.getSimpleName();

    new DynServletRegister().servletName(executorName).servletClass(ExecutorHandler.class)
        .urlPatterns(urlMappings.toArray(new String[urlMappings.size()])).multipartConfig(multipartConfig)
        .registerTo(sc);

    new DynFilterRegister().filterName(initializerName).filterClass(InitializerHandler.class).servletNames(executorName)
        .dispatcherTypes(DispatcherType.REQUEST).registerTo(sc);
  }
}
