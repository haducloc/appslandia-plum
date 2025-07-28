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

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CspReportTo extends InitializeObject {

  private String group;
  private Long max_age;
  private List<String> endpoints;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(this.group);
    Arguments.notNull(this.max_age);
    Arguments.hasElements(this.endpoints);
  }

  public String getGroup() {
    initialize();
    return this.group;
  }

  public CspReportTo setGroup(String group) {
    assertNotInitialized();
    this.group = group;
    return this;
  }

  public Long getMax_age() {
    initialize();
    return this.max_age;
  }

  public CspReportTo setMax_age(int max_age, TimeUnit unit) {
    assertNotInitialized();
    var ageInSec = TimeUnit.SECONDS.convert(max_age, unit);
    this.max_age = ValueUtils.valueOrMin(ageInSec, 0);
    return this;
  }

  public List<String> getEndpoints() {
    initialize();
    return this.endpoints;
  }

  public CspReportTo setEndpoints(List<String> endpoints) {
    assertNotInitialized();
    this.endpoints = endpoints;
    return this;
  }
}
