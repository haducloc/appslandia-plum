// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.RandomUtils;
import com.appslandia.common.utils.SecureRand;
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
  protected LanguageProvider languageProvider;

  @Inject
  protected ResourcesProvider resourcesProvider;

  @Inject
  protected ConverterProvider converterProvider;

  @Inject
  protected FormatProviderFactory formatProviderFactory;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ActionParser actionParser;

  @Inject
  protected RequestOriginParser requestOriginParser;

  @Inject
  protected PrefCookieHandler prefCookieHandler;

  @Inject
  protected TraceIdParser traceIdParser;

  protected String generateNonce() {
    var nonceSize = appConfig.getInt(AppConfig.CONFIG_NONCE_SIZE);
    var nonce = RandomUtils.nextBytes(nonceSize, SecureRand.getInstance());
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

    // PrefCookie
    var prefCookie = prefCookieHandler.loadPrefCookie(request, response);
    context.setPrefCookie(prefCookie);

    // PathItems
    var pathItems = parsePathItems(request);

    // Language
    initLangContext(request, context, pathItems);

    // ActionDesc
    var pathParamsOut = new Out<Map<String, String>>();
    var actionDesc = actionParser.parse(pathItems, pathParamsOut);
    context.setActionDesc(actionDesc);

    // PathParams
    context.setPathParams(pathParamsOut.value);

    var relativePath = getRelativePath(request, context);
    context.setRelativePath(relativePath);
    context.setRequestOrigin(requestOriginParser.parse(request));

    context.setModule(getModule(request, actionDesc, relativePath));
    context.setNonce(generateNonce());
    context.setTraceId(traceIdParser.parse(request));

    request.setAttribute(RequestContext.REQUEST_ATTRIBUTE_ID, context);
    return context;
  }

  protected String getRelativePath(HttpServletRequest request, RequestContext context) {
    var initCapacity = request.getServletPath().length();
    if (request.getPathInfo() != null) {
      initCapacity += request.getPathInfo().length();
    }
    var path = new StringBuilder(initCapacity);
    path.append(request.getServletPath());

    // Remove language
    if (context.isPathLanguage()) {
      path.delete(0, 1 + context.getLanguageId().length());
    }
    if (!path.isEmpty() && path.charAt(0) != '/') {
      path.insert(0, '/');
    }

    if (request.getPathInfo() != null) {
      path.append(request.getPathInfo());
    }
    return !path.isEmpty() ? path.toString() : "/";
  }

  protected void initLangContext(HttpServletRequest request, RequestContext context, List<String> pathItems) {
    Language language = null;
    if (languageProvider.isMultiLanguages()) {
      var testPathLanguage = !pathItems.isEmpty() ? pathItems.get(0) : null;

      if (testPathLanguage == null) {
        language = parseLanguage(request, context.getPrefCookie());

      } else {
        language = languageProvider.getLanguage(testPathLanguage);
        if (language == null) {
          language = parseLanguage(request, context.getPrefCookie());

        } else {
          context.setPathLanguage(true);
          pathItems.remove(0);
        }
      }
    } else {
      language = languageProvider.getDefaultLanguage();
    }

    context.setFormatProvider(formatProviderFactory.produce(language));
    context.setResources(resourcesProvider.getResources(language.getLocale()));
  }

  protected Language parseLanguage(HttpServletRequest request, PrefCookie prefCookie) {
    if (languageProvider.isMultiLanguages()) {
      var prefLang = prefCookie.getString(PrefCookie.PREF_LANGUAGE_ID);
      if (prefLang != null) {

        var language = languageProvider.getLanguage(prefLang);
        if (language != null) {
          return language;
        }
      }
      return languageProvider.resolveLanguage(request);
    }
    return languageProvider.getDefaultLanguage();
  }

  protected String getModule(HttpServletRequest request, ActionDesc actionDesc, String relativePath) {
    if (actionDesc != null) {
      return actionDesc.getModule();
    }

    var principal = ServletUtils.getPrincipal(request);
    if (principal != null) {
      return principal.getModule();
    }
    return appConfig.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
  }

  public static List<String> parsePathItems(HttpServletRequest request) {
    var servletPath = request.getServletPath();
    var pathInfo = request.getPathInfo();

    var startIdx = 0;
    int endIdx;
    List<String> items = new ArrayList<>();

    // servletPath
    while ((endIdx = servletPath.indexOf('/', startIdx)) != -1) {
      var pathItem = servletPath.substring(startIdx, endIdx);
      if (!pathItem.isEmpty()) {
        items.add(pathItem);
      }
      startIdx = endIdx + 1;
    }
    if (startIdx < servletPath.length()) {
      var pathItem = servletPath.substring(startIdx);
      if (!pathItem.isEmpty()) {
        items.add(pathItem);
      }
    }

    // pathInfo
    if (pathInfo != null) {
      startIdx = 0;
      while ((endIdx = pathInfo.indexOf('/', startIdx)) != -1) {
        var pathItem = pathInfo.substring(startIdx, endIdx);
        if (!pathItem.isEmpty()) {
          items.add(pathItem);
        }
        startIdx = endIdx + 1;
      }
      if (startIdx < pathInfo.length()) {
        var pathItem = pathInfo.substring(startIdx);
        if (!pathItem.isEmpty()) {
          items.add(pathItem);
        }
      }
    }
    return items;
  }
}
