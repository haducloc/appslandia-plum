// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

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

  ModuleConfigProvider getModuleConfigProvider();

  /**
   * Returns the supported view patterns, for example: "*.jsp, *.xhtml".
   *
   */
  String getSupportedViews();

  default DynMultipartConfig getMultipartConfig() {
    return new DynMultipartConfig();
  }

  default Class<? extends AppGlobalFilter> getAppGlobalFilter() {
    return AppGlobalFilter.class;
  }

  default String getAppGlobalFilterName() {
    return "__AppGlobalFilter";
  }

  default Class<? extends InitializerFilter> getInitializerFilter() {
    return InitializerFilter.class;
  }

  default String getInitializerFilterName() {
    return "__InitializerFilter";
  }

  default Class<? extends ContentHandlerFilter> getContentHandlerFilter() {
    return ContentHandlerFilter.class;
  }

  default String getContentHandlerFilterName() {
    return "__ContentHandlerFilter";
  }

  default Class<? extends ExecutorServlet> getExecutorServlet() {
    return ExecutorServlet.class;
  }

  default String getExecutorServletName() {
    return "__ExecutorServlet";
  }

  default void registerAppGlobalFilter(ServletContext ctx, boolean asyncSupported) {
    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', urlPatterns='/*', dispatcherTypes=DispatcherType.REQUEST, asyncSupported={}, isMatchAfter=false.",
        getAppGlobalFilter().getName(), getAppGlobalFilterName(), asyncSupported));

    new DynFilterRegistration().filterName(getAppGlobalFilterName()).filterClass(getAppGlobalFilter()).urlPatterns("/*")
        .dispatcherTypes(DispatcherType.REQUEST).asyncSupported(asyncSupported).isMatchAfter(false).registerTo(ctx);
  }

  default void registerInitializerFilter(ServletContext ctx, boolean asyncSupported) {
    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', dispatcherTypes=DispatcherType.REQUEST, asyncSupported={}, isMatchAfter=true.",
        getInitializerFilter().getName(), getInitializerFilterName(), getExecutorServletName(), asyncSupported));

    new DynFilterRegistration().filterName(getInitializerFilterName()).filterClass(getInitializerFilter())
        .servletNames(getExecutorServletName()).dispatcherTypes(DispatcherType.REQUEST).asyncSupported(asyncSupported)
        .isMatchAfter(true).registerTo(ctx);
  }

  default void registerContentHandlerFilter(ServletContext ctx, boolean asyncSupported) {
    var views = SplitUtils.splitByComma(getSupportedViews());
    Asserts.hasElements(views, "No views configured.");

    ctx.log(STR.fmt(
        "Registering filter '{}', filterName='{}', servletName='{}', urlPatterns='{}*', dispatcherTypes=[REQUEST, ASYNC*], asyncSupported={}, isMatchAfter=true.",
        getContentHandlerFilter().getName(), getContentHandlerFilterName(), getExecutorServletName(),
        String.join(", ", views), asyncSupported));

    if (asyncSupported) {
      // REQUEST to ExecutorServlet + ASYNC to views

      new DynFilterRegistration().filterName(getContentHandlerFilterName()).filterClass(getContentHandlerFilter())
          .servletNames(getExecutorServletName()).servletDispatcherTypes(DispatcherType.REQUEST).urlPatterns(views)
          .urlDispatcherTypes(DispatcherType.ASYNC).asyncSupported(true).isMatchAfter(true).registerTo(ctx);

    } else {
      // REQUEST to ExecutorServlet only
      new DynFilterRegistration().filterName(getContentHandlerFilterName()).filterClass(getContentHandlerFilter())
          .servletNames(getExecutorServletName()).servletDispatcherTypes(DispatcherType.REQUEST).asyncSupported(false)
          .isMatchAfter(true).registerTo(ctx);
    }
  }

  default void registerExecutorServlet(ServletContext ctx, boolean asyncSupported, boolean partSupported,
      String[] urlMappings) {
    var multipartConfig = partSupported ? getMultipartConfig() : null;

    ctx.log(STR.fmt("Registering servlet '{}', servletName='{}', multipartConfig={}, asyncSupported={}.",
        getExecutorServlet().getName(), getExecutorServletName(), multipartConfig, asyncSupported));

    new DynServletRegistration().servletName(getExecutorServletName()).servletClass(getExecutorServlet())
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

    var moduleConfigProvider = getModuleConfigProvider();
    for (var entry : moduleConfigProvider.getModuleConfigs().entrySet()) {

      // Exclude ModuleConfig.MODULE_NONE (__none)
      if (ModuleConfig.MODULE_NONE.equals(entry.getKey())) {
        continue;
      }
      var moduleConfig = entry.getValue();

      // /{language}
      for (Language language : moduleConfig.getLanguages()) {

        urlMappings.add(STR.fmt("/{}", language.getId()));
        urlMappings.add(STR.fmt("/{}/", language.getId()));
      }

      for (Class<?> controllerClass : controllers) {
        if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
          continue;
        }
        var controller = ActionDescUtils.getController(controllerClass);

        // /{controller}/*
        urlMappings.add(STR.fmt("/{}/*", controller));

        // /{language}/{controller}/*
        for (Language language : moduleConfig.getLanguages()) {
          urlMappings.add(STR.fmt("/{}/{}/*", language.getId(), controller));
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
      if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
        continue;
      }

      for (Method actionMethod : controllerClass.getMethods()) {
        if (!ActionDescUtils.isActionMethod(actionMethod) || actionMethod.getDeclaredAnnotation(Removed.class) != null
            || (matcher != null && !Boolean.TRUE.equals(matcher.apply(actionMethod)))) {
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
}
