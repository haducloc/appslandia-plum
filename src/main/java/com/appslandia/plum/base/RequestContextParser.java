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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.Language;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class RequestContextParser {

  @Inject
  protected PrefCookieHandler prefCookieHandler;

  @Inject
  protected LanguageProvider languageProvider;

  @Inject
  protected ResourcesProvider resourcesProvider;

  @Inject
  protected ConverterProvider converterProvider;

  @Inject
  protected FormatProviderManager formatProviderManager;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ActionParser actionParser;

  @Inject
  protected ClientIdParser clientIdParser;

  @Inject
  protected ServletModuleParser servletModuleParser;

  public RequestContext parse(HttpServletRequest request, HttpServletResponse response) {
    RequestContext context = (RequestContext) request.getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    if (context != null) {
      return context;
    }

    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST);

    // PrefCookie
    if (this.appConfig.getBool(AppConfig.CONFIG_ENABLE_PREF_COOKIE)) {
      this.prefCookieHandler.loadPrefCookie(request, response);
    }

    // Initialize RequestContext
    context = new RequestContext();
    context.setGetOrHead(HttpMethod.GET.equals(request.getMethod()) || HttpMethod.HEAD.equals(request.getMethod()));
    context.setConverterProvider(this.converterProvider);

    // Path Items
    List<String> pathItems = parsePathItems(request);
    String testPathLanguage = !pathItems.isEmpty() ? pathItems.get(0) : null;

    // Initialize localization context
    initLanguageContext(request, context, testPathLanguage);

    if (context.isPathLanguage()) {
      pathItems.remove(0);
    }

    // ActionDesc
    Map<String, String> pathParamMap = new HashMap<>();
    ActionDesc actionDesc = this.actionParser.parse(pathItems, pathParamMap);
    context.setActionDesc(actionDesc);
    context.setPathParamMap(Collections.unmodifiableMap(pathParamMap));

    context.setClientId(this.clientIdParser.parseId(request));
    context.setModule(getModule(request, actionDesc));

    // Browser Features
    String browserFeatures = ServletUtils.getCookieValue(request, BrowserFeatures.COOKIE_NAME);
    context.setBrowserFeatures((browserFeatures != null) ? ParseUtils.parseInt(browserFeatures, 0) : null);

    request.setAttribute(RequestContext.REQUEST_ATTRIBUTE_ID, context);
    return context;
  }

  protected void initLanguageContext(HttpServletRequest request, RequestContext context, String testPathLanguage) {
    Language language = null;
    if (testPathLanguage == null) {
      language = parseLanguage(request);
    } else {
      language = this.languageProvider.getLanguage(testPathLanguage);
      if (language == null) {
        language = parseLanguage(request);
      } else {
        context.setPathLanguage(true);
      }
    }
    context.setFormatProvider(this.formatProviderManager.get(language));
    context.setResources(this.resourcesProvider.getResources(language.getLanguageId()));
  }

  protected Language parseLanguage(HttpServletRequest request) {
    // Languages > 1?
    if (this.languageProvider.getLanguages().size() > 1) {

      PrefCookie prefCookie = (PrefCookie) request.getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID);
      if (prefCookie != null) {

        String prefLang = prefCookie.getString(PrefCookie.PARAM_LANGUAGE);
        if (prefLang != null) {

          Language language = this.languageProvider.getLanguage(prefLang);
          if (language != null) {
            return language;
          }
        }
      }
    }
    return this.languageProvider.getLanguage(request);
  }

  protected String getModule(HttpServletRequest request, ActionDesc actionDesc) {
    if (actionDesc != null) {
      return actionDesc.getModule();
    }

    String module = this.servletModuleParser.parseModule(request);
    if (module != null) {
      return module;
    }

    UserPrincipal principal = ServletUtils.getUserPrincipal(request);
    if (principal != null) {
      return principal.getModule();
    }
    return this.appConfig.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
  }

  public static List<String> parsePathItems(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String contextPath = request.getServletContext().getContextPath();

    int startIdx = 0;
    int endIdx;
    boolean isCtxPath = true;
    List<String> list = new ArrayList<>();

    while ((endIdx = requestURI.indexOf('/', startIdx)) != -1) {
      String pathItem = requestURI.substring(startIdx, endIdx);
      if (!pathItem.isEmpty()) {
        if (contextPath.isEmpty() || !isCtxPath) {
          list.add(pathItem);
        } else if (isCtxPath) {
          isCtxPath = false;
        }
      }
      startIdx = endIdx + 1;
    }
    if (startIdx < requestURI.length()) {
      String pathItem = requestURI.substring(startIdx);
      if (!pathItem.isEmpty()) {
        if (contextPath.isEmpty() || !isCtxPath) {
          list.add(pathItem);
        } else if (isCtxPath) {
          isCtxPath = false;
        }
      }
    }
    if (!list.isEmpty()) {
      String lastItem = list.get(list.size() - 1);
      // ;jsessionid=
      String incSid = String.format(";%s=",
          request.getServletContext().getSessionCookieConfig().getName().toLowerCase(Locale.ENGLISH));
      int idx = lastItem.indexOf(incSid);

      if (idx == 0) {
        list.remove(list.size() - 1);
      } else if (idx > 0) {
        list.set(list.size() - 1, lastItem.substring(0, idx));
      }
    }
    return list;
  }
}
