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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.json.JsonIgnore;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class Problem implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer status;
  private String title;
  private String detail;
  private String type;
  private String instance;
  private Map<String, Object> exts;

  @JsonIgnore
  private ResKey titleKey;

  @JsonIgnore
  private ResKey detailKey;

  @JsonIgnore
  private Throwable exception;

  public Integer getStatus() {
    return status;
  }

  public Problem setStatus(Integer status) {
    Arguments.isTrue((status == null) || ((400 <= status) && (status < 600)));
    this.status = status;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Problem setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getDetail() {
    return detail;
  }

  public Problem setDetail(String detail) {
    this.detail = detail;
    return this;
  }

  public String getType() {
    return type;
  }

  public Problem setType(String type) {
    this.type = type;
    return this;
  }

  public String getInstance() {
    return instance;
  }

  public Problem setInstance(String instance) {
    this.instance = instance;
    return this;
  }

  public Map<String, Object> getExts() {
    return exts;
  }

  public Problem setExts(Map<String, Object> exts) {
    this.exts = exts;
    return this;
  }

  public Problem setExt(String key, Object value) {
    Arguments.notNull(key);

    if (exts == null) {
      exts = new LinkedHashMap<>();
    }
    exts.put(key, value);
    return this;
  }

  public ResKey getTitleKey() {
    return titleKey;
  }

  public Problem setTitleKey(ResKey titleKey) {
    this.titleKey = titleKey;
    return this;
  }

  public ResKey getDetailKey() {
    return detailKey;
  }

  public Problem setDetailKey(ResKey detailKey) {
    this.detailKey = detailKey;
    return this;
  }

  public Throwable getException() {
    return exception;
  }

  public Problem setException(Throwable exception) {
    this.exception = exception;
    return this;
  }
}
