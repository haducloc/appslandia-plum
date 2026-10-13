// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Loc Ha
 *
 */
@NotLog
public class BusinessRuleException extends ProblemException {
  private static final long serialVersionUID = 1L;

  public BusinessRuleException(String devMessage, Integer status) {
    this(devMessage, status, null, null);
  }

  public BusinessRuleException(String devMessage, Integer status, String titleKey, ResKey detailKey) {
    super(devMessage, status, titleKey, detailKey, null, null, null);
  }

  public BusinessRuleException(String devMessage, Throwable cause, Integer status, String titleKey, ResKey detailKey) {
    super(devMessage, cause, status, titleKey, detailKey, null, null, null);
  }

  public BusinessRuleException(String devMessage, Integer status, String titleKey, ResKey detailKey, String type,
      Map<String, List<String>> errors, Map<String, Object> exts) {
    super(devMessage, status, titleKey, detailKey, type, errors, exts);
  }

  public BusinessRuleException(String devMessage, Throwable cause, Integer status, String titleKey, ResKey detailKey,
      String type, Map<String, List<String>> errors, Map<String, Object> exts) {
    super(devMessage, cause, status, titleKey, detailKey, type, errors, exts);
  }
}
