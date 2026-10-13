// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.appslandia.common.json.JsonIgnore;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;

import jakarta.json.bind.annotation.JsonbPropertyOrder;

/**
 *
 * @author Loc Ha
 *
 */
@JsonbPropertyOrder({ "status", "title", "detail", "type", "errors", "exts", "instance", "traceId" })
public class Problem implements Serializable {
  private static final long serialVersionUID = 1L;

  final Integer status;
  final String title;
  final String detail;
  final String type;
  final Map<String, List<String>> errors;
  final Map<String, Object> exts;
  final String instance;
  final String traceId;

  @JsonIgnore
  final Throwable exception;

  public Problem(Integer status, String title, String detail, String type, Map<String, List<String>> errors,
      Map<String, Object> exts, String instance, String traceId, Throwable exception) {

    Arguments.isTrue((status == null) || (400 <= status && status < 600), "status is invalid.");

    this.status = status;
    this.title = title;
    this.detail = detail;

    this.type = type;
    this.errors = CollectionUtils.toUnmodifiableMap(LinkedHashMap::new, errors);
    this.exts = CollectionUtils.toUnmodifiableMap(LinkedHashMap::new, exts);

    this.instance = instance;
    this.traceId = traceId;

    this.exception = exception;
  }

  public Integer getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public String getDetail() {
    return detail;
  }

  public String getType() {
    return type;
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }

  public Map<String, Object> getExts() {
    return exts;
  }

  public String getInstance() {
    return instance;
  }

  public String getTraceId() {
    return traceId;
  }

  public Throwable getException() {
    return exception;
  }
}
