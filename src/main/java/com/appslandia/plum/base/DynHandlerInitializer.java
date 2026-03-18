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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.STR;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

/**
 *
 * @author Loc Ha
 *
 */
@HandlesTypes({ DynHandlerConfig.class, LanguageConfig.class, ControllerBase.class })
public class DynHandlerInitializer implements ServletContainerInitializer {

  protected void registerAppFilterHandler(ServletContext ctx) {
    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', urlPatterns='/*', dispatcherTypes=DispatcherType.REQUEST, asyncSupported=false, isMatchAfter=false.",
        AppFilterHandler.class.getName(), DynHandlerConfig.APP_FILTER_HANDLER_NAME));

    new DynFilterRegister().filterName(DynHandlerConfig.APP_FILTER_HANDLER_NAME).filterClass(AppFilterHandler.class)
        .urlPatterns("/*").dispatcherTypes(DispatcherType.REQUEST).asyncSupported(false).isMatchAfter(false)
        .registerTo(ctx);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onStartup(Set<Class<?>> types, ServletContext ctx) throws ServletException {
    ctx.log("Running DynHandlerInitializer...");

    // AppFilterHandler
    registerAppFilterHandler(ctx);

    if (types == null || types.isEmpty()) {
      return;
    }

    Set<Class<?>> controllerClasses = new HashSet<>();
    Class<? extends DynHandlerConfig> handlerConfigClass = null;
    Class<? extends LanguageConfig> langConfigClass = null;

    for (Class<?> type : types) {
      if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
        continue;
      }

      // ControllerBase
      if (ControllerBase.class.isAssignableFrom(type)) {
        if (type.getDeclaredAnnotation(Removed.class) == null) {
          controllerClasses.add(type);
        }
      }

      // DynHandlerConfig
      else if (DynHandlerConfig.class.isAssignableFrom(type)) {
        if (type != DynHandlerConfig.Impl.class) {
          Asserts.isNull(handlerConfigClass, "Multiple DynHandlerConfig implementations found.");
          handlerConfigClass = (Class<? extends DynHandlerConfig>) type;
        }
      }

      // LanguageConfig
      else if (LanguageConfig.class.isAssignableFrom(type)) {
        Asserts.isNull(handlerConfigClass, "Multiple LanguageConfig implementations found.");
        langConfigClass = (Class<? extends LanguageConfig>) type;
      }
    }
    Asserts.notNull(langConfigClass, "No LanguageConfig implementation found.");

    if (handlerConfigClass == null) {
      handlerConfigClass = DynHandlerConfig.Impl.class;
    }

    var handlerConfig = ReflectionUtils.newInstance(handlerConfigClass);
    var languageConfig = ReflectionUtils.newInstance(langConfigClass);

    registerHandlers(ctx, controllerClasses, handlerConfig, languageConfig);
  }

  protected void registerHandlers(ServletContext ctx, Set<Class<?>> controllers, DynHandlerConfig handlerConfig,
      LanguageConfig langConfig) throws ServletException {

    Set<String> urlMappings = new TreeSet<>();

    // Home
    urlMappings.add("");

    // /{language}
    var languages = langConfig.get();
    if (languages.length > 1) {
      for (Language lang : languages) {

        urlMappings.add(STR.fmt("/{}", lang.getId()));
        urlMappings.add(STR.fmt("/{}/", lang.getId()));
      }
    }

    for (Class<?> controllerClass : controllers) {
      var controller = ActionDescProvider.getController(controllerClass);

      // /{controller}/*
      urlMappings.add(STR.fmt("/{}/*", controller));

      // /{language}/{controller}/*
      if (languages.length > 1) {
        for (Language lang : languages) {
          urlMappings.add(STR.fmt("/{}/{}/*", lang.getId(), controller));
        }
      }
    }

    // beforeInitializerHandler
    handlerConfig.beforeInitializerHandler(ctx);

    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', dispatcherTypes=DispatcherType.REQUEST, asyncSupported=false, isMatchAfter=false.",
        InitializerHandler.class.getName(), DynHandlerConfig.INITIALIZER_HANDLER_NAME,
        DynHandlerConfig.EXECUTOR_HANDLER_NAME));

    new DynFilterRegister().filterName(DynHandlerConfig.INITIALIZER_HANDLER_NAME).filterClass(InitializerHandler.class)
        .servletNames(DynHandlerConfig.EXECUTOR_HANDLER_NAME).dispatcherTypes(DispatcherType.REQUEST)
        .asyncSupported(false).isMatchAfter(false).registerTo(ctx);

    // afterInitializerHandler
    handlerConfig.afterInitializerHandler(ctx);

    // ExecutorHandler
    final var hasEnableParts = hasAction(m -> m.getDeclaredAnnotation(EnableParts.class) != null, controllers);
    final var hasEnableAsync = hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null, controllers);

    // DynMultipartConfig
    var multipartConfig = hasEnableParts ? handlerConfig.getMultipartConfig() : null;

    ctx.log(STR.fmt("Registering servlet '{}', servletName='{}', multipartConfig={}, asyncSupported={}.",
        ExecutorHandler.class.getName(), DynHandlerConfig.EXECUTOR_HANDLER_NAME, hasEnableParts, hasEnableAsync));

    new DynServletRegister().servletName(DynHandlerConfig.EXECUTOR_HANDLER_NAME).servletClass(ExecutorHandler.class)
        .urlPatterns(urlMappings.toArray(new String[urlMappings.size()])).multipartConfig(multipartConfig)
        .asyncSupported(hasEnableAsync).registerTo(ctx);

  }

  static void scanActions(Function<Method, Boolean> matcher, BiFunction<Class<?>, Method, Boolean> consumer,
      Set<Class<?>> controllers) {

    for (Class<?> controllerClass : controllers) {
      for (Method actionMethod : controllerClass.getMethods()) {

        if (!ActionDescUtils.isActionMethod(actionMethod)
            || ((matcher != null) && !Boolean.TRUE.equals(matcher.apply(actionMethod)))) {
          continue;
        }

        var stopped = consumer.apply(controllerClass, actionMethod);
        if (Boolean.TRUE.equals(stopped)) {
          return;
        }
      }
    }
  }

  static boolean hasAction(Function<Method, Boolean> matcher, Set<Class<?>> controllers) {
    final var out = new Out<Boolean>();
    scanActions(matcher, (controllerClass, actionMethod) -> {
      out.value = true;
      return true;
    }, controllers);
    return Boolean.TRUE.equals(out.value);
  }

  static boolean hasAnnotation(Class<? extends Annotation> annotation, Set<Class<?>> controllers) {
    return hasAction(m -> m.getDeclaredAnnotation(annotation) != null, controllers);
  }
}
