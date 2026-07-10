// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.plum.base.Message.MsgType;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.utils.ServletUtils;

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

  private MockHttpServletRequest initTempData(Out<String> tempDataId) throws Exception {
    var request = container.createRequest();
    var tempData = ServletUtils.getTempData(request);
    tempData.set("key1", "data1");

    var messages = ServletUtils.getMessages(request);
    messages.add(new Message(MsgType.INFO, "This is a test message."));

    tempDataId.value = tempDataManager.saveTempData(request, container.createResponse());
    Assertions.assertNotNull(tempDataId.value);

    return request;
  }

  @Test
  public void test_loadTempData() {
    try {
      var tempDataId = new Out<String>();
      var request = initTempData(tempDataId);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(TempDataManager.PARAM_TEMP_DATA_ID, tempDataId.value);

      var tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNotNull(tempData);
      Assertions.assertNotNull(getCurrentRequest().getAttribute(TempData.REQUEST_ATTRIBUTE_ID));

      Assertions.assertNotNull(tempData.get("key1"));
      Assertions.assertEquals("data1", tempData.get("key1"));

      var messages = ServletUtils.getMessages(getCurrentRequest());
      Assertions.assertNotNull(messages);
      Assertions.assertTrue(messages.size() == 1);

      tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse()); // Removed
      Assertions.assertNull(tempData);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadTempData_invalidTempDataId() {
    try {
      var tempDataId = new Out<String>();
      var request = initTempData(tempDataId);

      getCurrentRequest().setSession(request.getSession());
      getCurrentRequest().addParameter(TempDataManager.PARAM_TEMP_DATA_ID, "invalid");

      var tempData = tempDataManager.loadTempData(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNull(tempData);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
