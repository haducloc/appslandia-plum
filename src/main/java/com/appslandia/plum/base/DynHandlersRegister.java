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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class DynHandlersRegister implements Startup {

  protected String getInitializerHandler() {
    return "InitializerHandler";
  }

  protected String getExecutorHandler() {
    return "ExecutorHandler";
  }

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
  public void onStartup(ServletContext sc) throws ServletException {
    // ActionScanner
    var scanner = new ActionScanner();

    // Languages
    var languages = new Out<Language[]>();
    CDIUtils.consumeReference(CDI.current().getBeanManager(), LanguageSupplier.class, (supplier) -> {
      languages.value = supplier.get();
    });
    Asserts.notNull(languages.value, "No LanguageSupplier implemented.");

    sc.log(STR.fmt("Deploying languages: {}",
        String.join(", ", Arrays.stream(languages.value).map(l -> l.getId()).toList())));

    // urlMappings
    Set<String> urlMappings = new TreeSet<>();

    // Home
    urlMappings.add("");

    // /{language}
    for (Language lang : languages.value) {
      urlMappings.add(STR.fmt("/{}", lang.getId()));
      urlMappings.add(STR.fmt("/{}/", lang.getId()));
    }

    for (Class<?> controllerClass : scanner.getControllers()) {
      var controller = ActionDescProvider.getController(controllerClass);

      // /{controller}/*
      urlMappings.add(STR.fmt("/{}/*", controller));

      // /{language}/{controller}/*
      for (Language lang : languages.value) {
        urlMappings.add(STR.fmt("/{}/{}/*", lang.getId(), controller));
      }
    }

    final var hasEnableParts = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableParts.class) != null);
    final var hasEnableAsync = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null);

    // DynMultipartConfig
    var multipartConfig = hasEnableParts ? getMultipartConfig() : null;

    // ExecutorHandler
    sc.log(STR.fmt("Registering servlet '{}', servletName='{}', multipartConfig={}, asyncSupported={}.",
        ExecutorHandler.class.getName(), getExecutorHandler(), hasEnableParts, hasEnableAsync));

    new DynServletRegister().servletName(getExecutorHandler()).servletClass(ExecutorHandler.class)
        .urlPatterns(urlMappings.toArray(new String[urlMappings.size()])).multipartConfig(multipartConfig)
        .asyncSupported(hasEnableAsync).registerTo(sc);

    // InitializerHandler
    this.beforeInitializerHandler(sc, getExecutorHandler(), hasEnableAsync);

    sc.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', dispatcherTypes=DispatcherType.REQUEST, asyncSupported={}, isMatchAfter=true.",
        InitializerHandler.class.getName(), getInitializerHandler(), getExecutorHandler(), hasEnableAsync));

    new DynFilterRegister().filterName(getInitializerHandler()).filterClass(InitializerHandler.class)
        .servletNames(getExecutorHandler()).dispatcherTypes(DispatcherType.REQUEST).asyncSupported(hasEnableAsync)
        .isMatchAfter(true).registerTo(sc);

    this.afterInitializerHandler(sc, getExecutorHandler(), hasEnableAsync);
  }
}
