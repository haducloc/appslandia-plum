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

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;

import jakarta.servlet.MultipartConfigElement;

/**
 *
 * @author Loc Ha
 *
 */
public class DynMultipartConfig extends InitializeObject {

  private String location = StringUtils.EMPTY_STRING;
  private long maxFileSize = -1;
  private long maxRequestSize = -1;
  private int fileSizeThreshold;

  @Override
  protected void init() throws Exception {
  }

  public MultipartConfigElement toMultipartConfigElement() {
    initialize();
    return new MultipartConfigElement(this.location, this.maxFileSize, this.maxRequestSize, this.fileSizeThreshold);
  }

  public DynMultipartConfig location(String location) {
    assertNotInitialized();
    this.location = StringUtils.trimToEmpty(location);
    return this;
  }

  public DynMultipartConfig maxFileSize(long maxFileSize) {
    assertNotInitialized();
    this.maxFileSize = ValueUtils.valueOrMin(maxFileSize, -1);
    return this;
  }

  public DynMultipartConfig maxRequestSize(long maxRequestSize) {
    assertNotInitialized();
    this.maxRequestSize = ValueUtils.valueOrMin(maxRequestSize, -1);
    return this;
  }

  public DynMultipartConfig fileSizeThreshold(int fileSizeThreshold) {
    assertNotInitialized();
    this.fileSizeThreshold = ValueUtils.valueOrMin(fileSizeThreshold, 0);
    return this;
  }
}
