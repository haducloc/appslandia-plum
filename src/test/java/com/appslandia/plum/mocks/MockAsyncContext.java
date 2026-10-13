// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.io.IOException;

import com.appslandia.common.utils.ReflectionUtils;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockAsyncContext implements AsyncContext {

  final ServletRequest request;
  final ServletResponse response;

  private AsyncListener asyncListener;

  public MockAsyncContext(ServletRequest request, ServletResponse response) {
    this.request = request;
    this.response = response;
  }

  @Override
  public ServletRequest getRequest() {
    return request;
  }

  @Override
  public ServletResponse getResponse() {
    return response;
  }

  @Override
  public boolean hasOriginalRequestAndResponse() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispatch() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispatch(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispatch(ServletContext context, String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void complete() {
    if (asyncListener != null) {
      try {
        asyncListener.onComplete(new AsyncEvent(this));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public void start(Runnable task) {
    // No separate thread
    task.run();
  }

  @Override
  public void addListener(AsyncListener listener) {
    asyncListener = listener;
  }

  @Override
  public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
    try {
      return ReflectionUtils.newInstance(clazz);
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  @Override
  public void setTimeout(long timeout) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getTimeout() {
    throw new UnsupportedOperationException();
  }
}
