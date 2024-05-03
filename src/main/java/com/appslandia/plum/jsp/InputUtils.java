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

package com.appslandia.plum.jsp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class InputUtils {

  static final Map<String, Integer> DTN_INPUT_FEATURES;

  static {
    Map<String, Integer> map = new HashMap<>();

    map.put("number", BrowserFeatures.INPUT_NUMBER);
    map.put("range", BrowserFeatures.INPUT_RANGE);

    map.put("date", BrowserFeatures.INPUT_DATE);
    map.put("time", BrowserFeatures.INPUT_TIME);
    map.put("datetime-local", BrowserFeatures.INPUT_DATETIME_LOCAL);

    map.put("month", BrowserFeatures.INPUT_MONTH);
    map.put("week", BrowserFeatures.INPUT_WEEK);

    DTN_INPUT_FEATURES = Collections.unmodifiableMap(map);
  }

  public static Integer getDTNInputFeature(String type) {
    Asserts.notNull(type);

    return DTN_INPUT_FEATURES.get(type);
  }

  static final Set<String> UNLOCALIZED_TYPES = CollectionUtils.unmodifiableSet("hidden", "select", "checkbox", "radio");

  public static boolean getLocalize(HttpServletRequest request, String type) {
    if (type == null) {
      return true;
    }

    if (UNLOCALIZED_TYPES.contains(type)) {
      return false;
    }

    Integer feature = getDTNInputFeature(type);
    if (feature == null) {
      return true;
    }

    AppConfig appConfig = ServletUtils.getAppScoped(request.getServletContext(), AppConfig.class);
    if (!appConfig.getBool(AppConfig.CONFIG_INPUT_TYPE_BROWSER_FEATURE)) {
      return false;
    }

    RequestContext context = ServletUtils.getRequestContext(request);
    if (context.getBrowserFeatures() == null) {
      return false;
    }
    return (context.getBrowserFeatures() & feature) != feature;
  }
}
