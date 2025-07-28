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

/**
 *
 * @author Loc Ha
 *
 */
public class PathParam {

  private String paramName;
  private List<PathParam> subParams;

  public PathParam(String paramName) {
    this.paramName = paramName;
  }

  public PathParam(List<PathParam> subParams) {
    this.subParams = subParams;
  }

  public boolean hasPathParam(String paramName) {
    if (this.paramName != null) {
      if (this.paramName.equalsIgnoreCase(paramName)) {
        return true;
      }
    } else if (this.subParams.stream().anyMatch(p -> p.getParamName().equalsIgnoreCase(paramName))) {
      return true;
    }
    return false;
  }

  public String getParamName() {
    return this.paramName;
  }

  public List<PathParam> getSubParams() {
    return this.subParams;
  }
}
