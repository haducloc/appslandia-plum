// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.appslandia.common.json.JsonMap;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class TempData extends JsonMap {
  private static final long serialVersionUID = 1L;

  public static final String REQUEST_ATTRIBUTE_ID = "__temp_data";

  public TempData() {
    super(new LinkedHashMap<>());
  }

  public TempData(Map<String, Object> map) {
    super(map);
  }

  @Override
  public TempData set(String key, Object value) {
    super.set(key, value);
    return this;
  }

  @Override
  protected void validateValue(Object value) throws IllegalArgumentException {
    if (value instanceof Message) {
      return;
    }
    super.validateValue(value);
  }

  public TempData setMessages(List<Message> messages) {
    set(ServletUtils.REQUEST_ATTRIBUTE_MESSAGES, messages);
    return this;
  }

  public List<Message> exportMessages() {
    var messages = this.map.remove(ServletUtils.REQUEST_ATTRIBUTE_MESSAGES);
    return ObjectUtils.cast(messages);
  }
}
