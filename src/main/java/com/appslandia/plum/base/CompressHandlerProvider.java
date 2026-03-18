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

import java.util.Collections;
import java.util.Map;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class CompressHandlerProvider extends InitializingObject {

  private Map<String, CompressHandler> handlers = new CaseInsensitiveMap<>();

  @Override
  protected void init() throws Exception {
    handlers = Collections.unmodifiableMap(handlers);
  }

  public void registerHandler(String type, CompressHandler impl) {
    assertNotInitialized();
    handlers.put(type, impl);
  }

  public CompressHandler getHandler(String type) {
    initialize();
    var impl = handlers.get(type);
    return Arguments.notNull(impl, "No compressHandler is registered for type '{}'.", type);
  }

  public CompressHandler getHandler(HttpServletRequest request) {
    initialize();
    var type = ServletUtils.getContentEncoding(request, handlers.keySet());
    if (type == null) {
      return null;
    }
    return handlers.get(type);
  }
}
