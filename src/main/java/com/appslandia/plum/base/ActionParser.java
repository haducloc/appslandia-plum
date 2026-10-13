// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.StringOutput;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ActionParser {

  @Inject
  protected ActionDescProvider actionDescProvider;

  @Inject
  protected ModuleConfigProvider moduleConfigProvider;

  public ActionDesc parseAction(String ctxRelativePath, Out<String[]> pathItemsOut, Out<String> pathLanguageOut,
      Out<Map<String, String>> pathParamsOut, Out<Boolean> pathParamMismatchedOut) {

    var pathItems = Arrays.stream(ctxRelativePath.split("/")).filter(s -> !s.isEmpty()).toArray(String[]::new);
    pathItemsOut.value = pathItems;

    // Home
    if (pathItems.length == 0) {
      return actionDescProvider.getHomeDesc();
    }

    // /language or /controller
    if (pathItems.length == 1) {

      // /language
      var homeDesc = actionDescProvider.getHomeDesc();
      if (homeDesc != null) {

        if (moduleConfigProvider.getModuleConfig(homeDesc.getModule()).getLanguage(pathItems[0]) != null) {
          pathLanguageOut.value = pathItems[0];
          return homeDesc;
        }
      }

      // /controller
      var testRoute = new ActionRoute(null, pathItems[0], ServletUtils.ACTION_INDEX);

      var moduleRouteMap = actionDescProvider.getActionDescs().values().stream()
          .filter(routes -> routes.containsKey(testRoute)).findFirst().orElse(null);

      if (moduleRouteMap != null) {
        return moduleRouteMap.get(testRoute);
      }
      return null;
    }

    // > pathItems.length >= 2

    ActionDesc actionDesc = null;
    var pathParamOffset = 0;

    // /language/controller
    if (pathItems.length == 2) {
      var testRoute = new ActionRoute(pathItems[0], pathItems[1], ServletUtils.ACTION_INDEX);

      var moduleRouteMap = actionDescProvider.getActionDescs().values().stream()
          .filter(routeMap -> routeMap.containsKey(testRoute)).findFirst().orElse(null);

      if (moduleRouteMap != null) {
        pathLanguageOut.value = pathItems[0];
        return moduleRouteMap.get(testRoute);
      }
    }

    // /language/controller/action/*
    if (pathItems.length >= 3) {
      var testRoute = new ActionRoute(pathItems[0], pathItems[1], pathItems[2]);

      var moduleRouteMap = actionDescProvider.getActionDescs().values().stream()
          .filter(routeMap -> routeMap.containsKey(testRoute)).findFirst().orElse(null);

      if (moduleRouteMap != null) {
        pathLanguageOut.value = pathItems[0];
        actionDesc = moduleRouteMap.get(testRoute);
        pathParamOffset = 3;
      }
    }

    // /controller/action/*
    if (actionDesc == null) {
      var actionRoute = new ActionRoute(null, pathItems[0], pathItems[1]);

      var actionRouteMap = actionDescProvider.getActionDescs().values().stream()
          .filter(routeMap -> routeMap.containsKey(actionRoute)).findFirst().orElse(null);

      if (actionRouteMap != null) {
        actionDesc = actionRouteMap.get(actionRoute);
        pathParamOffset = 2;
      }
    }

    // No actionDesc
    if (actionDesc == null) {
      return null;
    }

    var pathParams = actionDesc.getPathParams();
    if (pathItems.length - pathParamOffset != pathParams.size()) {

      pathParamMismatchedOut.value = true;
      return actionDesc;
    }

    if (pathParams.isEmpty()) {
      return actionDesc;
    }

    // Parse PathParams
    var pathParamMap = new HashMap<String, String>();
    for (var i = 0; i < pathParams.size(); i++) {

      var pathParam = pathParams.get(i);
      var pathValue = pathItems[pathParamOffset + i];

      if (pathParam.getParamName() != null) {
        pathParamMap.put(pathParam.getParamName(), URLEncoding.decodePath(pathValue));

      } else {
        if (!parseSubParams(pathValue, pathParam.getSubParams(), pathParamMap)) {
          return null;
        }
      }
    }
    pathParamsOut.value = pathParamMap;
    return actionDesc;
  }

  public String toActionUrl(HttpServletRequest request, String controller, String action,
      Map<String, Object> parameters, boolean absoluteUrl) throws IllegalArgumentException {
    Arguments.notNull(controller);
    Arguments.notNull(action);

    var actionRoute = new ActionRoute(null, ActionDescUtils.toMvcRoute(controller), ActionDescUtils.toMvcRoute(action));
    var actionRouteMap = actionDescProvider.getActionDescs().values().stream()
        .filter(routeMap -> routeMap.containsKey(actionRoute)).findFirst().orElse(null);

    if (actionRouteMap == null) {
      throw new IllegalArgumentException(STR.fmt("The route '/{}/{}' is invalid.", controller, action));
    }

    var actionDesc = actionRouteMap.get(actionRoute);
    StringBuilder url = null;

    if (absoluteUrl) {
      url = ServletUtils.getHostUrl(request);
    } else {
      url = ServletUtils.newUriBuilder();
    }

    // Context path
    url.append(request.getContextPath());

    // Language
    var requestContext = ServletUtils.getRequestContext(request);
    var moduleConfig = moduleConfigProvider.getModuleConfig(actionDesc.getModule());

    if (moduleConfig.isMultiLanguages() && moduleConfig.getLanguage(requestContext.getLanguageId()) != null) {
      url.append('/').append(requestContext.getLanguageId());
    }

    // Controller
    url.append('/').append(actionRoute.controller);

    // Path Parameters
    if (actionDesc.getPathParams().size() > 0) {
      url.append('/').append(actionRoute.action);

      if (parameters != null) {
        addPathParams(url, parameters, actionDesc.getPathParams());
      } else {
        throw new IllegalArgumentException("Path parameters are required.");
      }
    } else {
      // Index
      if (!ServletUtils.ACTION_INDEX.equalsIgnoreCase(actionRoute.action)) {
        url.append('/').append(actionRoute.action);
      }
    }

    // Query Parameters
    if ((parameters != null) && (parameters.size() > actionDesc.getPathParamCount())) {
      addQueryParams(url, parameters, actionDesc.getPathParams());
    }
    return url.toString();
  }

  // pathItem: parameter(-parameter)+
  public static boolean parseSubParams(String pathItem, List<PathParam> subParams, Map<String, String> pathParamMap) {
    var nextIdx = subParams.size() - 1;
    var endIdx = pathItem.length();

    while (true) {
      // First parameter from the left
      if (nextIdx == 0) {
        if (endIdx <= 0) {
          return false;
        }

        var value = pathItem.substring(0, endIdx);
        if (value.isEmpty()) {
          return false;
        }

        pathParamMap.put(subParams.get(nextIdx).getParamName(), URLEncoding.decodePath(value));
        return true;
      }

      // Parse from right to left
      var startIdx = pathItem.lastIndexOf('-', endIdx - 1);
      if (startIdx < 0) {
        return false;
      }

      var value = pathItem.substring(startIdx + 1, endIdx);
      if (value.isEmpty()) {
        return false;
      }

      pathParamMap.put(subParams.get(nextIdx).getParamName(), URLEncoding.decodePath(value));

      endIdx = startIdx;
      nextIdx--;
    }
  }

  public static void addPathParams(StringBuilder url, Map<String, Object> parameters, List<PathParam> pathParams) {
    for (PathParam pathParam : pathParams) {
      if (pathParam.getParamName() != null) {

        var value = parameters.get(pathParam.getParamName());
        if (value == null) {
          throw new IllegalArgumentException(STR.fmt("Path parameter '{}' is required.", pathParam.getParamName()));
        }

        url.append('/').append(URLEncoding.encodePath(value.toString()));
        continue;
      }
      // Sub Parameters
      var isFirstSub = true;
      for (PathParam subParam : pathParam.getSubParams()) {

        var value = parameters.get(subParam.getParamName());
        if (value == null) {
          throw new IllegalArgumentException(STR.fmt("Path parameter '{}' is required.", subParam.getParamName()));
        }

        if (isFirstSub) {
          url.append('/').append(URLEncoding.encodePath(value.toString()));
          isFirstSub = false;
        } else {
          url.append('-').append(URLEncoding.encodePath(value.toString()));
        }
      }
    }
  }

  public static void addQueryParams(StringBuilder url, Map<String, Object> parameters, List<PathParam> pathParams) {
    var addedParam = false;

    for (Map.Entry<String, Object> param : parameters.entrySet()) {
      if ((param.getValue() == null) || pathParams.stream().anyMatch(p -> p.hasPathParam(param.getKey()))) {
        continue;
      }
      if (!addedParam) {
        url.append('?');
        addedParam = true;
      } else {
        url.append('&');
      }
      URLUtils.addQueryParam(url, param.getKey(), param.getValue());
    }
  }

  @SuppressWarnings("resource")
  public String toActionLink(HttpServletRequest request, String controller, String action,
      Map<String, Object> parameters, Map<String, Object> attributes, String labelText)
      throws IllegalArgumentException {
    Arguments.notNull(controller);
    Arguments.notNull(action);
    Arguments.notNull(labelText);

    var out = new StringOutput(255);

    out.write("<a href=\"");
    out.write(toActionUrl(request, controller, action, parameters, false));
    out.write('"');

    // Attributes
    if (attributes != null) {
      for (Entry<String, Object> attr : attributes.entrySet()) {
        if (attr.getValue() == null) {
          continue;
        }
        out.write(' ');
        out.write(attr.getKey());
        out.write("=\"");
        out.write(XmlEscaper.escapeXml(attr.getValue().toString()));
        out.write('"');
      }
    }

    out.write('>');
    out.write(XmlEscaper.escapeContent(labelText));
    out.write("</a>");

    return out.toString();
  }
}
