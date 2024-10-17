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

import com.appslandia.common.base.Language;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class DynHandlersRegister implements Startup {

  protected String getInitializerHandler() {
    return "InitializerHandler";
  }

  protected String getExecutorHandler() {
    return "ExecutorHandler";
  }

  protected abstract List<Language> getLanguages();

  protected DynMultipartConfig getMultipartConfig() {
    return new DynMultipartConfig();
  }

  protected void beforeInitializerHandler(ServletContext sc, String initializerHandlerServletName,
      boolean initializerHandlerAsyncSupported) {
  }

  protected void afterInitializerHandler(ServletContext sc, String initializerHandlerServletName,
      boolean initializerHandlerAsyncSupported) {
  }

  @Override
  public void onStartup(ServletContext sc, List<Class<? extends Startup>> startupClasses) throws ServletException {
    // ActionScanner
    ActionScanner scanner = ActionScanner.getInstance();
    Set<String> urlMappings = new TreeSet<>();

    // Home
    urlMappings.add("");

    List<Language> languages = getLanguages();
    Asserts.notNull(languages);

    // /{language}
    for (Language lang : languages) {
      urlMappings.add(STR.fmt("/{}", lang.getId()));
      urlMappings.add(STR.fmt("/{}/", lang.getId()));
    }

    for (Class<?> controllerClass : scanner.getControllerClasses()) {
      String controller = ActionDescProvider.getController(controllerClass);

      // /{controller}/*
      urlMappings.add(STR.fmt("/{}/*", controller));

      // /{language}/{controller}/*
      for (Language lang : languages) {
        urlMappings.add(STR.fmt("/{}/{}/*", lang.getId(), controller));
      }
    }

    final boolean hasEnableParts = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableParts.class) != null);
    final boolean hasEnableAsync = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null);

    // DynMultipartConfig
    DynMultipartConfig multipartConfig = hasEnableParts ? getMultipartConfig() : null;

    // ExecutorHandler
    new DynServletRegister().servletName(getExecutorHandler()).servletClass(ExecutorHandler.class)
        .urlPatterns(urlMappings.toArray(new String[urlMappings.size()])).multipartConfig(multipartConfig)
        .asyncSupported(hasEnableAsync).registerTo(sc);

    // InitializerHandler
    this.beforeInitializerHandler(sc, getExecutorHandler(), hasEnableAsync);

    new DynFilterRegister().filterName(getInitializerHandler()).filterClass(InitializerHandler.class)
        .servletNames(getExecutorHandler()).dispatcherTypes(DispatcherType.REQUEST).asyncSupported(hasEnableAsync)
        .isMatchAfter(true).registerTo(sc);

    this.afterInitializerHandler(sc, getExecutorHandler(), hasEnableAsync);
  }
}
