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

package com.appslandia.plum.tags;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.base.StringOutput;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class TagBuilder {

  final Map<String, Object> attributes = new LinkedHashMap<>();

  private String tagName;
  private boolean hasClosing;
  private String content;

  public String build() throws IOException {
    return build(128);
  }

  public String build(int initLength) throws IOException {
    Arguments.notNull(this.tagName);

    var out = new StringOutput(initLength);
    out.write('<');
    out.write(this.tagName);

    for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
      if (entry.getValue() != null) {
        HtmlUtils.escAttribute(out, entry.getKey(), entry.getValue().toString());
      }
    }

    if (!this.hasClosing) {
      out.write(" />");
    } else {
      out.write('>');
      if (this.content != null) {
        out.write(this.content);
      }
      out.write("</");
      out.write(this.tagName);
      out.write('>');
    }
    return out.toString();
  }

  public TagBuilder setTagName(String tagName) {
    this.tagName = tagName;
    return this;
  }

  public TagBuilder setHasClosing(boolean hasClosing) {
    this.hasClosing = hasClosing;
    return this;
  }

  public TagBuilder setContent(String content) {
    this.content = content;
    return this;
  }

  public TagBuilder setAttribute(String name, Object value) {
    this.attributes.put(name, value);
    return this;
  }
}
