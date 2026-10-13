// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.List;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class TempDataManager {

  public static final String PARAM_TEMP_DATA_ID = "__temp_data_id";

  protected abstract String doSaveTempData(HttpServletRequest request, HttpServletResponse response, TempData tempData);

  public String saveTempData(HttpServletRequest request, HttpServletResponse response) throws Exception {
    var tempData = buildTempData(request);
    if ((tempData == null) || tempData.isEmpty()) {
      return null;
    }
    var tempDataId = doSaveTempData(request, response, tempData);
    return tempDataId;
  }

  protected abstract TempData doLoadTempData(HttpServletRequest request, HttpServletResponse response,
      String tempDataId);

  public TempData loadTempData(HttpServletRequest request, HttpServletResponse response) {
    var tempDataId = request.getParameter(PARAM_TEMP_DATA_ID);
    if (tempDataId == null) {
      return null;
    }
    var tempData = doLoadTempData(request, response, tempDataId);
    if ((tempData == null) || tempData.isEmpty()) {
      return null;
    }

    // Messages
    var messages = tempData.exportMessages();
    if ((messages != null) && !messages.isEmpty()) {
      request.setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MESSAGES, messages);
    }

    request.setAttribute(TempData.REQUEST_ATTRIBUTE_ID, tempData);
    return tempData;
  }

  @SuppressWarnings("unchecked")
  protected TempData buildTempData(HttpServletRequest request) {
    var tempData = (TempData) request.getAttribute(TempData.REQUEST_ATTRIBUTE_ID);
    var messages = (List<Message>) request.getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MESSAGES);

    if ((messages != null) && !messages.isEmpty()) {
      if (tempData == null) {
        tempData = new TempData();
      }
      tempData.setMessages(messages);
    }
    return tempData;
  }
}
