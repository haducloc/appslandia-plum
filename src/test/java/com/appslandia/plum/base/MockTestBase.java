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

import org.junit.jupiter.api.extension.RegisterExtension;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.mocks.MockContainer;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockRequestLifecycleExtension;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.TestUtils;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class MockTestBase {

  protected final MockContainer container;

  public MockTestBase() {
    container = new MockContainer();
    initialize();

    MockContainer.containerHolder.set(container);
  }

  @RegisterExtension
  final MockRequestLifecycleExtension requestLifecycleExtension = new MockRequestLifecycleExtension();

  protected abstract void initialize();

  protected void execute(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
    var _currentRequest = MockContainer.currentRequestHolder.get();
    var _currentResponse = MockContainer.currentResponseHolder.get();

    try {
      MockContainer.currentRequestHolder.set(request);
      MockContainer.currentResponseHolder.set(response);

      container.execute(request, response);

    } finally {
      MockContainer.currentRequestHolder.set(_currentRequest);
      MockContainer.currentResponseHolder.set(_currentResponse);
    }
  }

  protected void executeCurrent(String method, String requestURL) throws Exception {
    var currentRequest = getCurrentRequest();
    var currentResponse = getCurrentResponse();
    currentRequest.setMethod(method);
    currentRequest.setRequestURL(requestURL);

    container.execute(currentRequest, currentResponse);
  }

  protected RequestContext getCurrentRequestContext() {
    return ServletUtils.getRequestContext(getCurrentRequest());
  }

  protected ModelState getCurrentModelState() {
    return ServletUtils.getModelState(getCurrentRequest());
  }

  protected MockHttpServletRequest getCurrentRequest() {
    return Asserts.notNull(MockContainer.currentRequestHolder.get());
  }

  protected MockHttpServletResponse getCurrentResponse() {
    return Asserts.notNull(MockContainer.currentResponseHolder.get());
  }

  protected void printCurrentException() {
    TestUtils.printException(getCurrentRequest());
  }

  protected void setRequestContextField(String fieldName, Object value) {
    var field = ReflectionUtils.findField(RequestContext.class, fieldName);
    Asserts.notNull(field);

    ReflectionUtils.set(field, getCurrentRequestContext(), value);
  }
}
