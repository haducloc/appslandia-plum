// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.util.Map;

import com.appslandia.common.json.JsonMapUtils;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.STR;

/**
 *
 * @author Loc Ha
 *
 */
public class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  final MsgType type;
  final String text;
  final boolean escXml;

  public Message(MsgType type, String text) {
    this(type, text, true);
  }

  public Message(MsgType type, String text, boolean escXml) {
    this.type = type;
    this.text = Arguments.notNull(text);
    this.escXml = escXml;
  }

  public Message(Map<String, Object> map) {
    this.type = JsonMapUtils.getEnum(map, "type", MsgType.class);
    this.text = JsonMapUtils.getStringReq(map, "text");
    this.escXml = JsonMapUtils.getBool(map, "escXml");
  }

  public MsgType getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public boolean isEscXml() {
    return escXml;
  }

  @Override
  public String toString() {
    return STR.fmt("type={}, text={}, escXml={}", type.name(), text, escXml);
  }

  public enum MsgType {
    INFO(1), NOTICE(2), WARN(3), ERROR(4);

    final int value;

    MsgType(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static MsgType parseType(String type) {
      if (type == null) {
        return null;
      }

      for (var msgType : values()) {
        if (msgType.name().equalsIgnoreCase(type)) {
          return msgType;
        }
      }
      throw new IllegalArgumentException(STR.fmt("type is invalid: {}", type));
    }
  }
}
