// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
