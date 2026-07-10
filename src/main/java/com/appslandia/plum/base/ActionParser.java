// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.StringOutput;
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
  protected LanguageProvider languageProvider;

  public ActionDesc parse(List<String> pathItems, Out<Map<String, String>> pathParamsOut) {
    // /
    // /language
    if (pathItems.size() == 0) {
      return actionDescProvider.getHomeDesc();
    }
    ActionDesc actionDesc = null;
    var controller = pathItems.get(0);

    // /controller
    if (pathItems.size() == 1) {
      actionDesc = actionDescProvider.getActionDesc(controller, ServletUtils.ACTION_INDEX);
      if ((actionDesc == null) || (actionDesc.getPathParams().size() > 0)) {
        return null;
      }
      return actionDesc;
    }
    // /controller/action(/parameter)*
    var action = pathItems.get(1);
    actionDesc = actionDescProvider.getActionDesc(controller, action);
    if (actionDesc == null) {
      return null;
    }

    // Remove controller/action
    pathItems.remove(0);
    pathItems.remove(0);

    var pathParams = actionDesc.getPathParams();
    if (pathItems.size() != pathParams.size()) {
      return null;
    }
    if (pathParams.isEmpty()) {
      return actionDesc;
    }

    // pathParamMap
    var pathParamMap = new HashMap<String, String>();

    for (var i = 0; i < pathParams.size(); i++) {
      var pathParm = pathParams.get(i);

      if (pathParm.getParamName() != null) {
        pathParamMap.put(pathParm.getParamName(), URLEncoding.decodePath(pathItems.get(i)));
      } else {
        if (!parseSubParams(pathItems.get(i), pathParm.getSubParams(), pathParamMap)) {
          return null;
        }
      }
    }

    pathParamsOut.value = Collections.unmodifiableMap(pathParamMap);
    return actionDesc;
  }

  public String toActionUrl(HttpServletRequest request, String controller, String action,
      Map<String, Object> parameters, boolean absoluteUrl) throws IllegalArgumentException {

    var actionDesc = actionDescProvider.getActionDesc(controller, action);
    if (actionDesc == null) {
      throw new IllegalArgumentException(STR.fmt("The controller/action '{}/{}' is invalid.", controller, action));
    }

    var requestContext = ServletUtils.getRequestContext(request);
    StringBuilder url = null;

    if (absoluteUrl) {
      url = ServletUtils.getHostUrl(request);
    } else {
      url = ServletUtils.newUriBuilder();
    }

    // Context path
    url.append(request.getContextPath());

    // Language
    if (languageProvider.isMultiLanguages()) {
      url.append('/').append(requestContext.getLanguageId());
    }

    // Controller
    url.append('/').append(controller);

    // Path Parameters
    if (actionDesc.getPathParams().size() > 0) {
      url.append('/').append(action);

      if (parameters != null) {
        addPathParams(url, parameters, actionDesc.getPathParams());
      } else {
        throw new IllegalArgumentException("Path parameters are required.");
      }
    } else {
      // Index
      if (!ServletUtils.ACTION_INDEX.equalsIgnoreCase(action)) {
        url.append('/').append(action);
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
