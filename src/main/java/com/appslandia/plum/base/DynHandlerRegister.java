// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.SplitUtils;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 *
 * @author Loc Ha
 *
 */
public interface DynHandlerRegister {

  Language[] getSupportedLanguages();

  /**
   * Returns the supported view patterns, for example: "*.jsp, *.xhtml".
   *
   */
  String getSupportedViews();

  default DynMultipartConfig getMultipartConfig() {
    return new DynMultipartConfig();
  }

  default String getAppGlobalFilterName() {
    return "__AppGlobalFilter";
  }

  default String getInitializerFilterName() {
    return "__InitializerFilter";
  }

  default String getContentHandlerFilterName() {
    return "__ContentHandlerFilter";
  }

  default String getExecutorServletName() {
    return "__ExecutorServlet";
  }

  default void registerAppGlobalFilter(ServletContext ctx, boolean asyncSupported) {
    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', urlPatterns='/*', dispatcherTypes=DispatcherType.REQUEST, asyncSupported={}, isMatchAfter=false.",
        AppGlobalFilter.class.getName(), getAppGlobalFilterName(), asyncSupported));

    new DynFilterRegistration().filterName(getAppGlobalFilterName()).filterClass(AppGlobalFilter.class)
        .urlPatterns("/*").dispatcherTypes(DispatcherType.REQUEST).asyncSupported(asyncSupported).isMatchAfter(false)
        .registerTo(ctx);
  }

  default void registerInitializerFilter(ServletContext ctx, boolean asyncSupported) {
    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', dispatcherTypes=DispatcherType.REQUEST, asyncSupported={}, isMatchAfter=true.",
        InitializerFilter.class.getName(), getInitializerFilterName(), getExecutorServletName(), asyncSupported));

    new DynFilterRegistration().filterName(getInitializerFilterName()).filterClass(InitializerFilter.class)
        .servletNames(getExecutorServletName()).dispatcherTypes(DispatcherType.REQUEST).asyncSupported(asyncSupported)
        .isMatchAfter(true).registerTo(ctx);
  }

  default void registerContentHandlerFilter(ServletContext ctx, boolean asyncSupported) {
    var views = SplitUtils.splitByComma(getSupportedViews());
    Asserts.hasElements(views, "No views configured.");

    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', urlPatterns='{}*', dispatcherTypes=[REQUEST, ASYNC*], asyncSupported={}, isMatchAfter=true.",
        ContentHandlerFilter.class.getName(), getContentHandlerFilterName(), getExecutorServletName(),
        String.join(", ", views), asyncSupported));

    if (asyncSupported) {
      // REQUEST to ExecutorServlet + ASYNC to views

      new DynFilterRegistration().filterName(getContentHandlerFilterName()).filterClass(ContentHandlerFilter.class)
          .servletNames(getExecutorServletName()).servletDispatcherTypes(DispatcherType.REQUEST).urlPatterns(views)
          .urlDispatcherTypes(DispatcherType.ASYNC).asyncSupported(true).isMatchAfter(true).registerTo(ctx);

    } else {
      // REQUEST to ExecutorServlet only
      new DynFilterRegistration().filterName(getContentHandlerFilterName()).filterClass(ContentHandlerFilter.class)
          .servletNames(getExecutorServletName()).servletDispatcherTypes(DispatcherType.REQUEST).asyncSupported(false)
          .isMatchAfter(true).registerTo(ctx);
    }
  }

  default void registerExecutorServlet(ServletContext ctx, boolean asyncSupported, boolean partSupported,
      String[] urlMappings) {
    var multipartConfig = partSupported ? getMultipartConfig() : null;

    ctx.log(STR.fmt("Registering servlet '{}', servletName='{}', multipartConfig={}, asyncSupported={}.",
        ExecutorServlet.class.getName(), getExecutorServletName(), multipartConfig, asyncSupported));

    new DynServletRegistration().servletName(getExecutorServletName()).servletClass(ExecutorServlet.class)
        .urlPatterns(urlMappings).multipartConfig(multipartConfig).asyncSupported(asyncSupported).registerTo(ctx);
  }

  default void onControllersDiscovered(ServletContext ctx, Set<Class<?>> controllers) {
    // EMPTY
  }

  default void onStartup(ServletContext ctx, Set<Class<?>> controllers) throws ServletException {
    // MVC Mappings
    Set<String> urlMappings = new TreeSet<>();

    // Home
    urlMappings.add("");

    // /{language}
    var languages = getSupportedLanguages();
    Asserts.hasElements(languages, "No languages configured.");

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

    var asyncSupported = hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null, controllers);
    var partSupported = hasAction(m -> m.getDeclaredAnnotation(EnablePart.class) != null, controllers);

    registerAppGlobalFilter(ctx, asyncSupported);
    registerInitializerFilter(ctx, asyncSupported);
    registerContentHandlerFilter(ctx, asyncSupported);
    registerExecutorServlet(ctx, asyncSupported, partSupported, urlMappings.toArray(String[]::new));

    onControllersDiscovered(ctx, controllers);
  }

  public static void scanActions(Function<Method, Boolean> matcher, BiFunction<Class<?>, Method, Boolean> consumer,
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

  public static boolean hasAction(Function<Method, Boolean> matcher, Set<Class<?>> controllers) {
    final var out = new Out<Boolean>();
    scanActions(matcher, (controllerClass, actionMethod) -> {
      out.value = true;
      return true;
    }, controllers);
    return Boolean.TRUE.equals(out.value);
  }

  public static boolean hasAnnotation(Class<? extends Annotation> annotation, Set<Class<?>> controllers) {
    return hasAction(m -> m.getDeclaredAnnotation(annotation) != null, controllers);
  }
}
