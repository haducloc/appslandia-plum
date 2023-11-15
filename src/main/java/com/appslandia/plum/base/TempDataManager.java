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

import com.appslandia.common.base.TextGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class TempDataManager {

  public static final String PARAM_TEMP_DATA_ID = "tempDataId";

  protected abstract TextGenerator getTempDataIdGenerator();

  protected abstract void doSaveTempData(HttpServletRequest request, HttpServletResponse response, String tempDataId,
      TempData tempData);

  public String saveTempData(HttpServletRequest request, HttpServletResponse response) throws Exception {
    TempData tempData = buildTempData(request);
    if ((tempData == null) || tempData.isEmpty()) {
      return null;
    }
    String tempDataId = getTempDataIdGenerator().generate();
    doSaveTempData(request, response, tempDataId, tempData);
    return tempDataId;
  }

  protected abstract TempData doLoadTempData(HttpServletRequest request, HttpServletResponse response,
      String tempDataId);

  public TempData loadTempData(HttpServletRequest request, HttpServletResponse response) {
    String tempDataId = request.getParameter(PARAM_TEMP_DATA_ID);
    if (tempDataId == null) {
      return null;
    }
    TempData tempData = doLoadTempData(request, response, tempDataId);
    if ((tempData == null) || tempData.isEmpty()) {
      return null;
    }

    // Export Messages
    Messages messages = tempData.exportMessages();
    if ((messages != null) && !messages.isEmpty()) {
      request.setAttribute(Messages.REQUEST_ATTRIBUTE_ID, messages);
    }
    request.setAttribute(TempData.REQUEST_ATTRIBUTE_ID, tempData);
    return tempData;
  }

  protected TempData buildTempData(HttpServletRequest request) {
    TempData tempData = (TempData) request.getAttribute(TempData.REQUEST_ATTRIBUTE_ID);
    Messages messages = (Messages) request.getAttribute(Messages.REQUEST_ATTRIBUTE_ID);

    if ((messages != null) && !messages.isEmpty()) {
      if (tempData == null) {
        tempData = new TempData();
      }
      tempData.importMessages(messages);
    }
    return tempData;
  }
}
