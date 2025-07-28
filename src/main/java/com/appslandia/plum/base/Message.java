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

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.STR;

/**
 *
 * @author Loc Ha
 *
 */
public class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final int TYPE_INFO = 1;
  public static final int TYPE_NOTICE = 2;
  public static final int TYPE_WARN = 3;
  public static final int TYPE_ERROR = 4;
  public static final int TYPE_FATAL = 5;

  public static final int TYPE_SUCCESS = 11;

  final int type;
  final String text;
  final boolean escXml;

  public Message(int type, String text) {
    this(type, text, true);
  }

  public Message(int type, String text, boolean escXml) {
    this.type = type;
    this.text = text;
    this.escXml = escXml;
  }

  public int getType() {
    return this.type;
  }

  public String getText() {
    return this.text;
  }

  public boolean isEscXml() {
    return this.escXml;
  }

  @Override
  public String toString() {
    return STR.fmt("type={}, text={}, escXml={}", toTypeName(this.type), this.text, this.escXml);
  }

  public static int parseType(String type) {
    Arguments.notNull(type);

    if ("info".equalsIgnoreCase(type)) {
      return TYPE_INFO;
    }
    if ("notice".equalsIgnoreCase(type)) {
      return TYPE_NOTICE;
    }
    if ("warn".equalsIgnoreCase(type)) {
      return TYPE_WARN;
    }
    if ("error".equalsIgnoreCase(type)) {
      return TYPE_ERROR;
    }
    if ("fatal".equalsIgnoreCase(type)) {
      return TYPE_FATAL;
    }
    if ("success".equalsIgnoreCase(type)) {
      return TYPE_SUCCESS;
    }
    throw new IllegalArgumentException("type is invalid.");
  }

  public static String toTypeName(int type) {
    return switch (type) {
    case TYPE_INFO -> "info";
    case TYPE_NOTICE -> "notice";
    case TYPE_WARN -> "warn";
    case TYPE_ERROR -> "error";
    case TYPE_FATAL -> "fatal";
    case TYPE_SUCCESS -> "success";
    default -> throw new IllegalArgumentException("type is invalid.");
    };
  }
}
