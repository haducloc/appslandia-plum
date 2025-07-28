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
  public static class TestController {

    @HttpPost
    public void testParam(Out<Integer> totalVocabs, RequestWrapper request) throws Exception {
      request.store("totalVocabs", totalVocabs);
    }

    @HttpPost
    public void testModel(@BindModel FlashcardModel model, RequestWrapper request) throws Exception {
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
