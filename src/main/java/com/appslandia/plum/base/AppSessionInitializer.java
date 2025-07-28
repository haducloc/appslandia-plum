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

import com.appslandia.common.base.Mutex;
import com.appslandia.common.utils.Asserts;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AppSessionInitializer {

  public static final String ATTRIBUTE_MUTEX = "mutex";

  @Inject
  protected AppConfig appConfig;

  public void sessionInitialized(@Observes @Initialized(SessionScoped.class) HttpSession session) {
    Asserts.isTrue(this.appConfig.isEnableSession(), "Http session is disabled.");

    session.setAttribute(ATTRIBUTE_MUTEX, new Mutex());
  }

  public void sessionDestroyed(@Observes @Destroyed(SessionScoped.class) HttpSession session) {
    try {
      session.removeAttribute(ATTRIBUTE_MUTEX);
    } catch (IllegalStateException ex) {
      // ignore
    }
  }
}
