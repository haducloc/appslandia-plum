// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ProblemException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final Integer status;
  private final String titleKey;
  private final ResKey detailKey;

  private final String type;
  private final Map<String, List<String>> errors;
  private final Map<String, Object> exts;

  public ProblemException(String devMessage, Integer status, String titleKey, ResKey detailKey, String type,
      Map<String, List<String>> errors, Map<String, Object> exts) {
    this(devMessage, null, status, titleKey, detailKey, type, errors, exts);
  }

  public ProblemException(String devMessage, Throwable cause, Integer status, String titleKey, ResKey detailKey,
      String type, Map<String, List<String>> errors, Map<String, Object> exts) {
    super(Arguments.notNull(devMessage, "devMessage is required."), cause);

    Arguments.isTrue((status == null) || ((400 <= status) && (status < 600)), "status is invalid.");
    this.status = status;

    this.titleKey = titleKey;
    this.detailKey = detailKey;

    this.type = type;
    this.errors = CollectionUtils.toUnmodifiableMap(LinkedHashMap::new, errors);
    this.exts = CollectionUtils.toUnmodifiableMap(LinkedHashMap::new, exts);
  }

  public Integer getStatus() {
    return status;
  }

  public String getTitleKey() {
    return titleKey;
  }

  public ResKey getDetailKey() {
    return detailKey;
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

  // @formatter:off
  @Override
  public String toString() {
    return super.toString() +
        " {status=" + status +
        ", titleKey=" + titleKey +
        ", detailKey=" + detailKey +
        ", type=" + type +
        ", errors=" + errors +
        ", exts=" + exts +
        '}';
    // @formatter:on
  }
}
