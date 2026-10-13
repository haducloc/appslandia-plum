// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.SecureRandoms;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class RequestContextParser {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ActionParser actionParser;

  @Inject
  protected ResourcesProvider resourcesProvider;

  @Inject
  protected ConverterProvider converterProvider;

  @Inject
  protected FormatProviderFactory formatProviderFactory;

  @Inject
  protected ModuleConfigProvider moduleConfigProvider;

  @Inject
  protected PrefCookieHandler prefCookieHandler;

  @Inject
  protected ActionDescProvider actionDescProvider;

  @Inject
  protected TraceIdParser traceIdParser;

  @Inject
  protected RequestOriginParser requestOriginParser;

  protected String generateNonce() {
    var nonceSize = appConfig.getInt(AppConfig.CONFIG_NONCE_SIZE);
    var nonce = SecureRandoms.nextBytes(nonceSize);
    return BaseEncoder.BASE64_URL_NP.encode(nonce);
  }

  public RequestContext initRequestContext(HttpServletRequest request, HttpServletResponse response) {
    var context = (RequestContext) request.getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    if (context != null) {
      return context;
    }
    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST);

    // Initialize RequestContext
    context = new RequestContext();
    context.setContextPath(request.getContextPath());
    context.setGetOrHead(HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod()));
    context.setConverterProvider(converterProvider);

    // ActionDesc
    var pathItemsOut = new Out<String[]>();
    var pathLanguageOut = new Out<String>();
    var pathParamsOut = new Out<Map<String, String>>();
    var pathParamMismatchedOut = new Out<Boolean>();

    var ctxRelativePath = getContextRelativePath(request);

    // Notes: if actionDesc != null, pathLanguageOut.value can be not null.
    // Notes: if actionDesc == null, pathLanguageOut.value must be null

    var actionDesc = actionParser.parseAction(ctxRelativePath, pathItemsOut, pathLanguageOut, pathParamsOut,
        pathParamMismatchedOut);

    context.setActionDesc(actionDesc);
    context.setPathParams(pathParamsOut.value);
    context.setPathParamMismatched(Boolean.TRUE.equals(pathParamMismatchedOut.value));

    // module
    var module = (actionDesc != null) ? actionDesc.getModule()
        : resolveModule(request, pathItemsOut.value, pathLanguageOut);

    context.setModule(module);
    context.setPathLanguage(pathLanguageOut.value != null);

    // Handling Path
    String handlingPath = null;
    if (actionDesc != null) {
      handlingPath = actionDesc.getMvcPath();
    } else {
      handlingPath = resolveHandlingPath(ctxRelativePath, pathItemsOut.value);
    }
    context.setHandlingPath(handlingPath);

    // PrefCookie
    var prefCookie = prefCookieHandler.loadPrefCookie(request, response, module);
    context.setPrefCookie(prefCookie);

    var language = (pathLanguageOut.value != null)
        ? moduleConfigProvider.getModuleConfig(module).getLanguage(pathLanguageOut.value)
        : resolveLanguage(request, prefCookie, module);

    context.setFormatProvider(formatProviderFactory.produce(language));
    context.setResources(resourcesProvider.getResources(module, language));

    // Other
    context.setNonce(generateNonce());
    context.setTraceId(traceIdParser.parse(request));
    context.setRequestOrigin(requestOriginParser.parse(request));

    request.setAttribute(RequestContext.REQUEST_ATTRIBUTE_ID, context);
    return context;
  }

  protected String resolveHandlingPath(String ctxRelativePath, String[] pathItems) {
    var handlingPath = ctxRelativePath;

    if (pathItems.length > 0) {
      var posPathLang = pathItems[0];

      // Exclude path language
      if (moduleConfigProvider.getModuleConfigs().values().stream()
          .anyMatch(mc -> mc.getLanguage(posPathLang) != null)) {

        handlingPath = ctxRelativePath.substring(1 + posPathLang.length());
      }
    }
    return !handlingPath.isEmpty() ? handlingPath : "/";
  }

  protected String resolveModule(HttpServletRequest request, String[] pathItems, Out<String> pathLanguageOut) {
    String module = null;
    if (pathItems.length >= 2) {

      // /language/controller
      module = actionDescProvider.getControllerModules().get(pathItems[1]);
      if (module != null) {

        if (moduleConfigProvider.getModuleConfig(module).getLanguage(pathItems[0]) != null) {
          pathLanguageOut.value = pathItems[0];
        } else {
          module = null;
        }
      }
    }

    // /controller
    if (module == null && pathItems.length >= 1) {
      module = actionDescProvider.getControllerModules().get(pathItems[0]);
    }
    if (module != null) {
      return module;
    }

    // Resolve module from other rules
    if (appConfig.getEnableModules().size() == 1) {
      module = appConfig.getEnableModules().iterator().next();

    } else if (request.getUserPrincipal() != null) {
      module = ServletUtils.getPrincipal(request).getModule();

    } else {
      module = moduleConfigProvider.getFallbackModule();
    }

    // Final attempt to resolve the path language
    if (pathItems.length > 0) {
      if (moduleConfigProvider.getModuleConfig(module).getLanguage(pathItems[0]) != null) {
        pathLanguageOut.value = pathItems[0];
      }
    }
    return module;
  }

  protected Language resolveLanguage(HttpServletRequest request, PrefCookie prefCookie, String module) {
    // NONE module
    if (ModuleConfig.MODULE_NONE.equals(module)) {
      return moduleConfigProvider.getModuleConfig(module).getDefaultLanguage();
    }

    var moduleConfig = moduleConfigProvider.getModuleConfig(module);

    // Multi-language?
    if (moduleConfig.isMultiLanguages()) {

      var prefLang = prefCookie.getString(PrefCookie.PREF_LANGUAGE_ID);
      if (prefLang != null) {
        var language = moduleConfig.getLanguage(prefLang);
        if (language != null) {
          return language;
        }
      }
      return moduleConfig.resolveLanguage(request);
    }

    // Single language
    return moduleConfig.getDefaultLanguage();
  }

  public static String getContextRelativePath(HttpServletRequest request) {
    var servletPath = request.getServletPath();
    var pathInfo = request.getPathInfo();

    if (pathInfo == null) {
      return servletPath.isEmpty() ? "/" : servletPath;
    }
    return servletPath + pathInfo;
  }
}
