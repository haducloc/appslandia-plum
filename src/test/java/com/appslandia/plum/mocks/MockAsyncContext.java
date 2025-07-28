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
    return this.request;
  }

  @Override
  public ServletResponse getResponse() {
    return this.response;
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
    if (this.asyncListener != null) {
      try {
        this.asyncListener.onComplete(new AsyncEvent(this));
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
    this.asyncListener = listener;
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
