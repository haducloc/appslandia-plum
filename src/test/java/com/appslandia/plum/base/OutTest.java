// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Out;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class OutTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testParam() {
    try {
      getCurrentRequest().addParameter("totalVocabs", "1000");
      executeCurrent("POST", "http://localhost/app/testController/testParam");

      @SuppressWarnings("unchecked")
      var totalVocabs = (Out<Integer>) getCurrentRequest().getAttribute("totalVocabs");

      Assertions.assertNotNull(totalVocabs);
      Assertions.assertNotNull(totalVocabs.value);
      Assertions.assertTrue(totalVocabs.value == 1000);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testParam_null() {
    try {
      executeCurrent("POST", "http://localhost/app/testController/testParam");

      @SuppressWarnings("unchecked")
      var totalVocabs = (Out<Integer>) getCurrentRequest().getAttribute("totalVocabs");

      Assertions.assertNotNull(totalVocabs);
      Assertions.assertNull(totalVocabs.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testModel() {
    try {
      getCurrentRequest().addParameter("totalVocabs", "1000");
      executeCurrent("POST", "http://localhost/app/testController/testModel");

      var model = (FlashcardModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);

      Assertions.assertNotNull(model);
      Assertions.assertNotNull(model.totalVocabs);
      Assertions.assertNotNull(model.totalVocabs.value);
      Assertions.assertTrue(model.totalVocabs.value == 1000);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testModel_null() {
    try {
      getCurrentRequest().addParameter("totalVocabs", "");
      executeCurrent("POST", "http://localhost/app/testController/testModel");

      var model = (FlashcardModel) getCurrentRequest().getAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL);

      Assertions.assertNotNull(model);
      Assertions.assertNotNull(model.totalVocabs);
      Assertions.assertNull(model.totalVocabs.value);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController extends ControllerBase {

    @HttpPost
    public void testParam(Out<Integer> totalVocabs, HttpRequestFacade request) throws Exception {
      request.store("totalVocabs", totalVocabs);
    }

    @HttpPost
    public void testModel(@BindModel FlashcardModel model, HttpRequestFacade request) throws Exception {
      request.storeModel(model);
    }
  }

  public static class FlashcardModel {
    private Out<Integer> totalVocabs;

    public Out<Integer> getTotalVocabs() {
      return totalVocabs;
    }

    public void setTotalVocabs(Out<Integer> totalVocabs) {
      this.totalVocabs = totalVocabs;
    }
  }
}
