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
 * @see <a href="https://tools.ietf.org/html/rfc7807"></a>
 */
public class Problem implements Serializable {
  private static final long serialVersionUID = 1L;

  // 4XX, 5XX
  private Integer status;

  @JsonIgnore
  private ResKey titleKey;
  private String title;

  @JsonIgnore
  private ResKey detailKey;
  private String detail;

  private String type;
  private String instance;
  private Map<String, Object> extensions;

  @JsonIgnore
  private Throwable exception;
  private String stackTrace;

  private ModelState modelState;

  public Integer getStatus() {
    return this.status;
  }

  public Problem setStatus(Integer status) {
    Arguments.isTrue((status == null) || ((400 <= status) && (status < 600))); // 4XX, 5XX
    this.status = status;
    return this;
  }

  public ResKey getTitleKey() {
    return this.titleKey;
  }

  public Problem setTitleKey(ResKey titleKey) {
    this.titleKey = titleKey;
    return this;
  }

  public Problem setTitleKey(String titleKey) {
    return setTitleKey(new ResKey(titleKey));
  }

  public String getTitle() {
    return this.title;
  }

  public Problem setTitle(String title) {
    this.title = title;
    return this;
  }

  public ResKey getDetailKey() {
    return this.detailKey;
  }

  public Problem setDetailKey(ResKey detailKey) {
    this.detailKey = detailKey;
    return this;
  }

  public Problem setDetailKey(String detailKey) {
    return setDetailKey(new ResKey(detailKey));
  }

  public String getDetail() {
    return this.detail;
  }

  public Problem setDetail(String detail) {
    this.detail = detail;
    return this;
  }

  public String getType() {
    return this.type;
  }

  public Problem setType(String type) {
    this.type = type;
    return this;
  }

  public String getInstance() {
    return this.instance;
  }

  public Problem setInstance(String instance) {
    this.instance = instance;
    return this;
  }

  public Map<String, Object> getExtensions() {
    return this.extensions;
  }

  public Problem setExtensions(Map<String, Object> extensions) {
    this.extensions = extensions;
    return this;
  }

  public Problem addExtension(String key, Object value) {
    Arguments.notNull(key);

    if (this.extensions == null) {
      this.extensions = new LinkedHashMap<>();
    }
    this.extensions.put(key, value);
    return this;
  }

  public Throwable getException() {
    return this.exception;
  }

  public Problem setException(Throwable exception) {
    this.exception = exception;
    return this;
  }

  public String getStackTrace() {
    return this.stackTrace;
  }

  public Problem setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public ModelState getModelState() {
    return this.modelState;
  }

  public Problem setModelState(ModelState modelState) {
    this.modelState = modelState;
    return this;
  }
}
