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

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class DynHandlersRegister implements Startup {

  protected String getExecutorHandlerName() {
    return "ExecutorHandler";
  }

  protected String getExecutorHandlerNameAsync() {
    return "ExecutorHandlerAsync";
  }

  protected String getInitializerHandlerName() {
    return "InitializerHandler";
  }

  protected String getInitializerHandlerNameAsync() {
    return "InitializerHandlerAsync";
  }

  protected abstract String[] getLanguageIds();

  protected DynMultipartConfig buildMultipartConfig() {
    return new DynMultipartConfig();
  }

  @Override
  public void onStartup(ServletContext sc, List<Class<? extends Startup>> startupClasses) throws ServletException {
    ActionScanner scanner = ActionScanner.getInstance();
    Set<String> urlMappings = new TreeSet<>();

    // Home
    urlMappings.add("");

    String[] languages = getLanguageIds();
    Asserts.notNull(languages);

    // /{language}
    for (String language : languages) {
      urlMappings.add(STR.fmt("/{}", language));
      urlMappings.add(STR.fmt("/{}/", language));
    }

    for (Class<?> controllerClass : scanner.getControllerClasses()) {
      String controller = ActionDescProvider.getController(controllerClass);

      // /{controller}/*
      urlMappings.add(STR.fmt("/{}/*", controller));

      // /{language}/{controller}/*
      for (String language : languages) {
        urlMappings.add(STR.fmt("/{}/{}/*", language, controller));
      }
    }

    // EnableParts
    boolean hasEnableParts = scanner.hasAction(
        m -> m.getDeclaredAnnotation(EnableParts.class) != null && m.getDeclaredAnnotation(EnableAsync.class) == null);

    // DynMultipartConfig
    DynMultipartConfig multipartConfig = hasEnableParts ? buildMultipartConfig() : null;

    // ExecutorHandler
    new DynServletRegister().servletName(getExecutorHandlerName()).servletClass(ExecutorHandler.class)
        .urlPatterns(urlMappings.toArray(new String[urlMappings.size()])).multipartConfig(multipartConfig)
        .registerTo(sc);

    // InitializerHandler
    new DynFilterRegister().filterName(getInitializerHandlerName()).filterClass(InitializerHandler.class)
        .servletNames(getExecutorHandlerName()).dispatcherTypes(DispatcherType.REQUEST).registerTo(sc);

    // EnableAsync
    if (scanner.hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null)) {

      final Set<String> mappingsAsync = new TreeSet<>();
      final Out<Boolean> hasPartsAsync = new Out<Boolean>(false);

      scanner.scanActions(m -> m.getDeclaredAnnotation(EnableAsync.class) != null, (c, m) -> {
        String controller = ActionDescProvider.getController(c);
        String action = ActionDescProvider.getAction(m);

        // /{controller}/{action}
        if (ServletUtils.ACTION_INDEX.equals(action)) {
          mappingsAsync.add(STR.fmt("/{}", controller));
          mappingsAsync.add(STR.fmt("/{}/", controller));
        }
        mappingsAsync.add(STR.fmt("/{}/{}", controller, action));
        mappingsAsync.add(STR.fmt("/{}/{}/", controller, action));

        // /{language}/{controller}/{action}
        for (String language : languages) {
          if (ServletUtils.ACTION_INDEX.equals(action)) {
            mappingsAsync.add(STR.fmt("/{}/{}", language, controller));
            mappingsAsync.add(STR.fmt("/{}/{}/", language, controller));
          }
          mappingsAsync.add(STR.fmt("/{}/{}/{}", language, controller, action));
          mappingsAsync.add(STR.fmt("/{}/{}/{}/", language, controller, action));
        }

        // EnableParts
        if (!hasPartsAsync.value) {
          hasPartsAsync.value = m.getDeclaredAnnotation(EnableParts.class) != null;
        }
        return false;
      });

      // DynMultipartConfig
      DynMultipartConfig multipartConfigAsync = hasPartsAsync.value ? buildMultipartConfig() : null;

      // ExecutorHandler ASYNC
      new DynServletRegister().servletName(getExecutorHandlerNameAsync()).servletClass(ExecutorHandler.class)
          .urlPatterns(mappingsAsync.toArray(new String[mappingsAsync.size()])).multipartConfig(multipartConfigAsync)
          .asyncSupported(true).registerTo(sc);

      // InitializerHandler ASYNC
      new DynFilterRegister().filterName(getInitializerHandlerNameAsync()).filterClass(InitializerHandler.class)
          .servletNames(getExecutorHandlerNameAsync()).asyncSupported(true).dispatcherTypes(DispatcherType.REQUEST)
          .registerTo(sc);
    }
  }
}
