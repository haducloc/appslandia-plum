// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class TempDataManagerTest extends MockTestBase {

  TempDataManager tempDataManager;

  @Override
  protected void initialize() {
    tempDataManager = container.getObject(TempDataManager.class);
  }

  private String initTempData(MockHttpServletRequest request) throws Exception {
    var tempData = new TempData().set("key1", "data1");
    request.setAttribute(TempData.REQUEST_ATTRIBUTE_ID, tempData);
    return tempDataManager.saveTempData(request, container.createResponse());
  }

  @Test
  public void test_saveTempData() {
    try {
      var tempData = new TempData().set("key1", "data1");
      getCurrentRequest().setAttribute(TempData.REQUEST_ATTRIBUTE_ID, tempData);

      var tempDataId = tempDataManager.saveTempData(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNotNull(tempDataId);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadTempData() {
    try {
      var request = container.createRequest();
      var tempDataId = initTempData(request);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId);

      var tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNotNull(tempData);
      Assertions.assertNotNull(getCurrentRequest().getAttribute(TempData.REQUEST_ATTRIBUTE_ID));

      Assertions.assertNotNull(tempData.get("key1"));
      Assertions.assertEquals("data1", tempData.get("key1"));

      tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse()); // Removed
      Assertions.assertNull(tempData);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadTempData_invalidTempDataId() {
    try {
      var request = container.createRequest();
      initTempData(request);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(TempDataManager.PARAM_TEMP_DATA_ID, "invalid");

      var tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNull(tempData);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
